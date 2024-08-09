package me.ryster.nheen.codegen

import me.ryster.nheen.ir.Instruction
import me.ryster.nheen.ir.Literal
import me.ryster.nheen.ir.Operation
import me.ryster.nheen.ir.Value
import me.ryster.nheen.runtime.language.core.RuntimeObject
import me.ryster.nheen.runtime.language.core.objects.InteiroObject
import me.ryster.nheen.runtime.language.core.objects.TextoObject
import me.ryster.nheen.runtime.language.io.Console

class JvmIrTransformer(
    val packageName: String,
    val functionName: String,
    val ir: List<Instruction>
) {
    private var variables = mutableMapOf<String, Int>()
    private var currentVariableIdx: Int = 1

    fun usedVariables() = currentVariableIdx


    fun doTransform(): List<JvmIr> {
        val src = mutableListOf<JvmIr>()

        for (instruction in ir) {
            when (instruction) {
                is Instruction.Assign -> {
                    val id = variables.getOrPut(instruction.variable) {
                        currentVariableIdx++
                    }


                    transformValue(instruction.value, src)
                    src += JvmIr.StoreReference(id)
                }

                is Instruction.Call -> {
                    transformCall(instruction, src)
                }

                Instruction.ReturnVoid -> {
                    src += JvmIr.ReturnVoid
                }
            }
        }

        return src
    }

    private fun transformCall(
        instruction: Instruction.Call,
        src: MutableList<JvmIr>
    ) {
        instruction.arguments.forEach {
            transformValue(it, src)
        }

        val className = when (instruction.function) {
            "imprima" -> Console::class.java
            "lerInteiro" -> Console::class.java
            else -> throw IllegalArgumentException("Unknown function: ${instruction.function}")
        }

        val targetFunction = when (instruction.function) {
            "imprima" -> "imprima"
            "lerInteiro" -> "lerInteiro"
            else -> throw IllegalArgumentException("Unknown function: ${instruction.function}")
        }

        val obj = RuntimeObject::class.java.name.replace(".", "/")
        if (instruction.function == "imprima") {
            src += JvmIr.StaticCall(
                className.name,
                targetFunction,
                "(L$obj;)V",
                emptyList()
            )
            return
        } else {
            src += JvmIr.StaticCall(
                className.name,
                targetFunction,
                "()L$obj;",
                emptyList()
            )
        }
    }

    private fun transformOperation(
        operation: Operation,
        src: MutableList<JvmIr>
    ) {
        val obj = RuntimeObject::class.java.name.replace(".", "/")
        when (operation) {
            is Operation.Plus -> {
                transformValue(operation.left, src)
                transformValue(operation.right, src)
                src += JvmIr.InvokeVirtual(
                    RuntimeObject::class.java,
                    "plus",
                    "(L$obj;)L$obj;",
                    emptyList()
                )
            }

            is Operation.Divide -> TODO()
            is Operation.Minus -> TODO()
            is Operation.Multiply -> TODO()
        }
    }


    private fun transformValue(
        it: Value,
        src: MutableList<JvmIr>
    ) {
        when (it) {
            is Value.OperationChain -> {
                transformOperation(it.operation, src)
            }

            is Value.Raw -> {
                transformLiteral(it.literal, src)
            }

            is Value.Variable -> {
                val id = variables[it.name]!!
                src += JvmIr.LoadReference(id)
            }

            is Value.FunctionCall -> {
                transformCall(it.instruction, src)
            }
        }
    }

    private fun transformLiteral(
        literal: Literal,
        src: MutableList<JvmIr>,
    ) {
        when (literal) {
            is Literal.Inteiro -> {
                src += JvmIr.NewObject(InteiroObject::class.java)
                src += JvmIr.PushInt(literal.value)
                src += JvmIr.InvokeSpecial(
                    InteiroObject::class.java,
                    "<init>",
                    "(I)V",
                    emptyList()
                )
            }

            is Literal.Texto -> {
                src += JvmIr.NewObject(TextoObject::class.java)
                src += JvmIr.PushString(literal.value)
                src += JvmIr.InvokeSpecial(
                    TextoObject::class.java,
                    "<init>",
                    "(Ljava/lang/String;)V",
                    emptyList()
                )
            }
        }
    }
}