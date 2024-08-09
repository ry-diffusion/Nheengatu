package me.ryster.nheen.visitor

import me.ryster.nheen.grammar.NheenBaseVisitor
import me.ryster.nheen.grammar.NheenParser
import me.ryster.nheen.ir.Literal.*
import me.ryster.nheen.ir.*

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
        ctx.statements().children.forEach {
            println("<-- ${it.text}")
            visit(it)
        }
    }

    override fun visitDecl(ctx: NheenParser.DeclContext) {
        val name = ctx.Identifier().text
        val value = ctx.expr()
        val parsedValue = parseValue(value)
        println(" --> Processando declaração: $name = $parsedValue")
        ir += Instruction.Assign(name, parsedValue)
    }

    override fun visitFunctionCall(ctx: NheenParser.FunctionCallContext?) {
        val callable = ctx!!.Identifier()
        val args = mutableListOf<Value>()

        ctx.expr().forEach {
            args.add(parseValue(it))
        }

        println(" --> Processando chamada de função: ${callable.text}")
        ir += Instruction.Call(packageName!!, callable.text, args)
    }



    private fun toValue(literal: NheenParser.TextLiteralContext): Value.Raw {
        val text = literal.text.substring(1, literal.text.length - 1)
        println(" --> Processando valor: (TEXTO) $text")
        return Value.Raw(Texto(text))
    }

    private fun toValue(literal: NheenParser.NumberLiteralContext): Value.Raw {
        println(" --> Processando valor: (NUMERO) ${literal.text}")
        return Value.Raw(Inteiro(literal.text.toInt()))
    }

    private fun toValue(variable: NheenParser.VariableReferenceContext): Value.Variable {
        println(" --> Processando valor: (VARIAVEL) ${variable.text}")
        return Value.Variable(variable.text)
    }


    private fun parseValue(ctx: NheenParser.ExprContext): Value {
        return when (val child = ctx.getChild(0)) {
            is NheenParser.ValueContext -> {
                when (val valueChild = child.getChild(0)) {
                    is NheenParser.TextLiteralContext -> toValue(valueChild)
                    is NheenParser.NumberLiteralContext -> toValue(valueChild)
                    is NheenParser.VariableReferenceContext -> toValue(valueChild)

                    else -> throw Error("Tipo de valor não suportado: ${valueChild::class}")
                }
            }

            is NheenParser.OperatorChainContext -> {
                TODO()
            }

            else -> throw Error("Tipo de contexto não suportado: ${child::class}")
        }
    }
}
