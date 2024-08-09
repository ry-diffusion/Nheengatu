package me.ryster.nheen.visitor

import me.ryster.nheen.grammar.NheenBaseVisitor
import me.ryster.nheen.grammar.NheenParser
import me.ryster.nheen.ir.Instruction
import me.ryster.nheen.ir.Literal.*
import me.ryster.nheen.ir.Value

class TreeToIRVisitor : NheenBaseVisitor<Unit>() {
    private val ir: MutableList<Instruction> = mutableListOf()
    private var packageName: String? = null

    fun getInstructions(): List<Instruction> = ir
    fun getPackageName(): String = packageName!!

    override fun visitFile(ctx: NheenParser.FileContext) {
        packageName = ctx.pacote().Identifier().text
        println("--> Processando pacote $packageName")

        visit(ctx.inicio())
        ir += Instruction.ReturnVoid

        println(" -> Processamento do bloco de início concluído")
    }

    override fun visitInicio(ctx: NheenParser.InicioContext) {
        println(" -> Processando bloco de início")
        ctx.statements().children.forEach { visit(it) }
    }

    override fun visitDecl(ctx: NheenParser.DeclContext) {
        val name = ctx.Identifier().text
        val value = ctx.expr()
        when (val child = value.getChild(0)) {
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
                visit(child)
                val call = ir.removeAt(ir.size - 1)
                if (call !is Instruction.Call) {
                    throw Error("Última instrução não é uma chamada de função!")
                }
                ir += Instruction.Assign(name, Value.FunctionCall(call))
            }
            is NheenParser.BinaryOperationChainContext -> {
                TODO()
            }
            else -> {
                throw Error("(DECLARAÇÃO NÃO SUPORTADA!) $name = ${value.text}")
            }
        }
    }

    override fun visitFunctionCall(ctx: NheenParser.FunctionCallContext) {
        val callable = ctx.Identifier()
        val args = mutableListOf<Value>()

        ctx.expr().forEach {
            when (val child = it.getChild(0)) {
                is NheenParser.NumeroContext -> {
                    println(" --> Processando argumento: (NUMERO) ${it.text}")
                    args.add(Value.Raw(Inteiro(it.text.toInt())))
                }
                is NheenParser.TextoContext -> {
                    val text = it.text.substring(1, it.text.length - 1)
                    println(" --> Processando argumento: (TEXTO) $text")
                    args.add(Value.Raw(Texto(text)))
                }
                is NheenParser.VariableReferenceContext -> {
                    println(" --> Processando argumento: (VARIAVEL) ${it.text}")
                    args.add(Value.Variable(it.text))
                }
                else -> {
                    throw Error("(ARGUMENTO NÃO SUPORTADO!) ${it.text} (${it.ruleContext.text})")
                }
            }
        }

        println(" --> Processando chamada de função: ${callable.text}")
        ir += Instruction.Call(packageName!!, callable.text, args)
    }
}
