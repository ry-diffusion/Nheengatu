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

    fun parseOperation(ctx: NheenParser.OperatorChainContext?): Operation {
        val left = convertValue(ctx!!.value(0))
        var lastOperation: Operation? = null

        ctx.operator().forEachIndexed { index, op ->
            val operator = op.text
            val operand = convertValue(ctx.value(index + 1))

            val operation = when (operator) {
                "+" -> Operation.Plus(left, operand)
                "-" -> Operation.Minus(left, operand)
                "*" -> Operation.Multiply(left, operand)
                "/" -> Operation.Divide(left, operand)
                else -> TODO("Unsupported Operation")
            }


            if (null != lastOperation) {
                val leftOperand = Value.OperationChain(lastOperation!!)

                lastOperation = when (operator) {
                    "+" -> Operation.Plus(leftOperand, operand)
                    "-" -> Operation.Minus(leftOperand, operand)
                    "*" -> Operation.Multiply(leftOperand, operand)
                    "/" -> Operation.Divide(leftOperand, operand)
                    else -> throw Error("Operação não suportada: $operator")
                }
            } else {
                lastOperation = operation
            }
        }

        return lastOperation!!
    }

    private fun toValue(variable: NheenParser.VariableReferenceContext): Value.Variable {
        println(" --> Processando valor: (VARIAVEL) ${variable.text}")
        return Value.Variable(variable.text)
    }

    private fun convertValue(ctx: NheenParser.ValueContext): Value = when (val valueChild = ctx.getChild(0)) {
        is NheenParser.TextLiteralContext -> toValue(valueChild)
        is NheenParser.NumberLiteralContext -> toValue(valueChild)
        is NheenParser.VariableReferenceContext -> toValue(valueChild)
        is NheenParser.ExpressionParenContext -> parseValue(valueChild.expr())
        is NheenParser.OperatorChainContext ->
            Value.OperationChain(parseOperation(valueChild))

        else -> throw Error("Tipo de valor não suportado: ${valueChild::class}")
    }


    private fun parseValue(ctx: NheenParser.ExprContext): Value = when (val child = ctx.getChild(0)) {
        is NheenParser.ValueContext -> convertValue(child)
        is NheenParser.OperatorChainContext -> Value.OperationChain(parseOperation(child))
        else -> throw Error("Tipo de contexto não suportado: ${child::class}")
    }
}
