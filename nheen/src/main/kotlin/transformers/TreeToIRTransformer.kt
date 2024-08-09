package me.ryster.nheen.transformers

import me.ryster.nheen.grammar.NheenParser
import me.ryster.nheen.ir.Instruction
import me.ryster.nheen.ir.Literal.*
import me.ryster.nheen.ir.Value
import org.antlr.v4.runtime.tree.ParseTree

class TreeToIRTransformer {
    var packages: Map<String, List<Instruction>> = emptyMap()

    fun transformTree(tree: NheenParser.FileContext) {
        val ir: Ir = mutableListOf()
        val pkg = tree.pacote()
        val packageName = pkg.Identifier().text
        println("--> Processando pacote $packageName")

        val inicio = tree.inicio()
        println(" -> Processando bloco de início")
        val instructions = inicio.statements()

        instructions.children.forEach {
            transformChild(packageName, it, ir)
        }

        ir += Instruction.ReturnVoid

        println(" -> Processamento do bloco de início concluído")
        packages += Pair(packageName, ir)
    }

    private fun transformChild(
        packageName: String,
        it: ParseTree,
        ir: Ir
    ) {
        when (val child = it.getChild(0)) {
            is NheenParser.DeclContext -> {
                transformDeclaration(packageName, child, ir)
            }

            is NheenParser.ExprContext -> {
                transformExpression(packageName, ir, it, child)
            }

            else -> {
                throw Error("(INSTRUÇÃO NÃO SUPORTADA!) ${it.text}")
            }
        }
    }

    private fun transformExpression(packageName: String, ir: Ir, it: ParseTree, child: ParseTree) {
        println(" --> Processando expressão: ${it.text}")

        when (val elem = child.getChild(0)) {
            is NheenParser.FunctionCallContext -> {
                transformFunctionCall(elem, ir, packageName)
            }

            else -> {
                throw Error("(EXPRESSÃO NÃO SUPORTADA!) ${it.text}")
            }
        }
    }

    private fun transformFunctionCall(
        elem: NheenParser.FunctionCallContext,
        ir: Ir,
        packageName: String
    ) {
        val callable = elem.Identifier()
        val args = mutableListOf<Value>()

        elem.expr().forEach {
            when (it.getChild(0)) {
                is NheenParser.NumeroContext -> {
                    println(" --> Processando argumento: (NUMERO) ${it.text}")
                    args.add(
                        Value.Raw(
                            Inteiro(it.text.toInt())
                        )
                    )
                }

                is NheenParser.TextoContext -> {
                    val text = it.text.substring(1, it.text.length - 1)
                    println(" --> Processando argumento: (TEXTO) $text")
                    args.add(
                        Value.Raw(
                            Texto(text)
                        )
                    )
                }

                is NheenParser.VariableReferenceContext -> {
                    println(" --> Processando argumento: (VARIAVEL) ${it.text}")
                    args.add(
                        Value.Variable(it.text)
                    )
                }

                else -> {
                    throw Error("(ARGUMENTO NÃO SUPORTADO!) ${it.text} (${it.ruleContext.text}")
                }
            }
        }

        println(" --> Processando chamada de função: ${callable.text}")
        ir += Instruction.Call(packageName, callable.text, args)
    }

    private fun transformDeclaration(
        packageName: String,
        child: NheenParser.DeclContext,
        ir: Ir
    ) {
        val name = child.Identifier().text
        val value = child.expr()
        when (val it = value.getChild(0)) {
            is NheenParser.NumeroContext -> {
                println(" --> Processando declaração: (NUMERO) $name = ${value.text}")
                ir += Instruction.Assign(name, Value.Raw(Inteiro(value.text.toInt())))
            }

            is NheenParser.TextoContext -> {
                var text = value.text
                text = text.substring(1, text.length - 1)
                println(" --> Processando declaração: (TEXTO) $name = $text")
                ir += Instruction.Assign(name, Value.Raw(Texto(text)))
            }

            is NheenParser.VariableReferenceContext -> {
                println(" --> Processando declaração: (VARIAVEL) $name = ${value.text}")
                ir += Instruction.Assign(name, Value.Variable(value.text))
            }

            is NheenParser.FunctionCallContext -> {
                println(" --> Processando declaração: (CHAMADA DE FUNÇÃO) $name = ${value.text}")
                transformFunctionCall(it, ir, packageName)
                val call = ir.removeLast()
                if (call !is Instruction.Call) {
                    throw Error("Última instrução não é uma chamada de função!")
                }
                ir += Instruction.Assign(name, Value.FunctionCall(call))
            }

            else -> {
                throw Error("(DECLARAÇÃO NÃO SUPORTADA!) $name = ${value.text}")
            }
        }
    }
}

typealias Ir = MutableList<Instruction>