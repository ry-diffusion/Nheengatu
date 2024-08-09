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

    private fun parseOperation(ctx: NheenParser.OperatorChainContext?): Operation {
        val precedence = mapOf(
            "+" to 1,
            "-" to 1,
            "*" to 2,
            "/" to 2
        )

        val output = mutableListOf<Any>()
        val operators = mutableListOf<String>()

        val values = ctx!!.value()
        val operatorsCtx = ctx.operator()

        for (i in values.indices) {
            output.add(convertValue(values[i]))
            if (i < operatorsCtx.size) {
                val op = operatorsCtx[i].text
                while (operators.isNotEmpty() && precedence[operators.last()]!! >= precedence[op]!!) {
                    output.add(operators.removeAt(operators.size - 1))
                }
                operators.add(op)
            }
        }

        while (operators.isNotEmpty()) {
            output.add(operators.removeAt(operators.size - 1))
        }

        val stack = mutableListOf<Value>()

        for (token in output) {
            when (token) {
                is Value -> stack.add(token)
                is String -> {
                    val right = stack.removeAt(stack.size - 1)
                    val left = stack.removeAt(stack.size - 1)
                    val operation = when (token) {
                        "+" -> Operation.Plus(left, right)
                        "-" -> Operation.Minus(left, right)
                        "*" -> Operation.Multiply(left, right)
                        "/" -> Operation.Divide(left, right)
                        else -> throw Error("Unsupported Operation: $token")
                    }
                    stack.add(Value.OperationChain(operation))
                }
            }
        }

        return (stack[0] as Value.OperationChain).operation
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
