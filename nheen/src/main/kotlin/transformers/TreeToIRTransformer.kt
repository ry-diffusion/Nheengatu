package me.ryster.nheen.transformers

import me.ryster.nheen.grammar.NheenParser
import me.ryster.nheen.ir.Instruction
import org.antlr.v4.runtime.tree.ParseTree

class TreeToIRTransformer {
    var packages: Map<String, List<Instruction>> = emptyMap()

    fun transformTree(tree: NheenParser.FileContext) {
        val ir: Ir = mutableListOf()
        val pkg = tree.pacote()
        val pkgName = pkg.Identifier().text
        println("--> Processando pacote $pkgName")

        val inicio = tree.inicio()
        println(" -> Processando bloco de início")
        val instructions = inicio.statements()

        instructions.children.forEach {
            transformChild(it, ir)
        }

        ir += Instruction.ReturnVoid

        println(" -> Processamento do bloco de início concluído")
        packages += Pair(pkgName, ir)
    }

    private fun transformChild(
        it: ParseTree,
        ir: Ir
    ) {
        when (val child = it.getChild(0)) {
            is NheenParser.DeclContext -> {
                transformDeclaration(child, ir)
            }

            is NheenParser.ExprContext -> {
                transformExpression(ir, it, child)
            }

            else -> {
                throw Error("(INSTRUÇÃO NÃO SUPORTADA!) ${it.text}")
            }
        }
    }

    private fun transformExpression(ir: Ir, it: ParseTree, child: ParseTree) {
        var registerIdx = 0
        println(" --> Processando expressão: ${it.text}")
        when (val elem = child.getChild(0)) {
            is NheenParser.FunctionCallContext -> {
                val callable = elem.Identifier()
                val args = mutableListOf<Instruction.Register>()

                elem.expr().forEach {
                   when (it.getChild(0)) {
                        is NheenParser.NumeroContext -> {
                            println(" --> Processando argumento: (NUMERO) ${it.text}")
                            val register = Instruction.Register.Variable(registerIdx)
                            ir += Instruction.PushValue(register, Instruction.SimpleValue.Inteiro(it.text.toInt()))
                            registerIdx++
                            args.add(register)
                        }

                        is NheenParser.TextoContext-> {
                            println(" --> Processando argumento: (TEXTO) ${it.text}")
                            val register = Instruction.Register.Variable(registerIdx)
                            ir += Instruction.PushValue(register, Instruction.SimpleValue.Texto(it.text))
                            registerIdx++
                            args.add(register)
                        }

                       is NheenParser.VariableReferenceContext -> {
                            println(" --> Processando argumento: (VARIABLE_REFERENCE) ${it.text}")
                            val register = Instruction.Register.Variable(registerIdx)
                            ir += Instruction.PushVariable(register, it.text)
                            registerIdx++
                            args.add(register)
                        }

                        else -> {
                            throw Error("(ARGUMENTO NÃO SUPORTADO!) ${it.text} (${it.ruleContext.text}")
                        }
                    }
                }

                println(" --> Processando chamada de função: ${callable.text}")
                ir += Instruction.Call(callable.text, args)
            }

            else -> {
                throw Error("(EXPRESSÃO NÃO SUPORTADA!) ${it.text}")
            }
        }
    }

    private fun transformDeclaration(
        child: NheenParser.DeclContext,
        ir: Ir
    ) {
        val name = child.Identifier().text
        val type = child.tipo().text
        val value = child.expr()
        when (value.getChild(0)) {
            is NheenParser.NumeroContext -> {
                println(" --> Processando declaração: (NUMERO) $name = ${value.text}")
                ir += Instruction.Assign(name, Instruction.SimpleValue.Inteiro(value.text.toInt()))
            }

            is NheenParser.TextoContext -> {
                println(" --> Processando declaração: (TEXTO) $name = ${value.text}")
                ir += Instruction.Assign(name, Instruction.SimpleValue.Texto(value.text))
            }

            else -> {
                throw Error("(DECLARAÇÃO NÃO SUPORTADA!) $type $name = ${value.text}")
            }
        }
    }
}

typealias Ir = MutableList<Instruction>