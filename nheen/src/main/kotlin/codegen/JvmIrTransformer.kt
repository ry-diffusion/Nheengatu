package me.ryster.nheen.codegen

import me.ryster.nheen.ir.Instruction
import me.ryster.nheen.ir.Literal
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


                    when (val value = instruction.value) {
                        is Value.Raw -> {
                            transformLiteral(value.literal, src)
                        }

                        is Value.Variable -> {
                            val id = variables[value.name]!!
                            src += JvmIr.LoadReference(id)
                        }
                    }

                    src += JvmIr.StoreReference(id)

                }

                is Instruction.Call -> {
                    instruction.arguments.forEach {
                        when (it) {
                            is Value.Raw -> {
                                transformLiteral(it.literal, src)
                            }

                            is Value.Variable -> {
                                val id = variables[it.name]!!
                                src += JvmIr.LoadReference(id)
                            }
                        }
                    }

                    val className = when (instruction.function) {
                        "imprima" -> Console::class.java
                        else -> throw IllegalArgumentException("Unknown function: ${instruction.function}")
                    }
                    val targetFunction = when (instruction.function) {
                        "imprima" -> "imprima"
                        else -> throw IllegalArgumentException("Unknown function: ${instruction.function}")
                    }

                    val obj = RuntimeObject::class.java.name.replace(".", "/")
                    src += JvmIr.StaticCall(
                        className.name,
                        targetFunction,
                        "(L$obj;)V",
                        emptyList()
                    )
                }

                Instruction.ReturnVoid -> {
                    src += JvmIr.ReturnVoid
                }
            }
        }

        return src
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