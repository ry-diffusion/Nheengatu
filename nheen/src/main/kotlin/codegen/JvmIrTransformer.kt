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
    private val callResolver: JvmCallResolver,
    private val ir: List<Instruction>
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

        src += callResolver.resolve(instruction.function)
    }

    private fun transformOperation(
        operation: Operation,
        src: MutableList<JvmIr>
    ) {
        transformValue(operation.left, src)
        transformValue(operation.right, src)

        src += callResolver.resolveOperation(operation)
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
                    InteiroObject::class.java.canonicalName,
                    "<init>",
                    "(I)V",
                    emptyList()
                )
            }

            is Literal.Texto -> {
                src += JvmIr.NewObject(TextoObject::class.java)
                src += JvmIr.PushString(literal.value)
                src += JvmIr.InvokeSpecial(
                    TextoObject::class.java.name,
                    "<init>",
                    "(Ljava/lang/String;)V",
                    emptyList()
                )
            }
        }
    }
}