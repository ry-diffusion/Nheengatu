package me.ryster.nheen.codegen

import javassist.ClassPool
import javassist.CtClass
import javassist.CtNewMethod
import javassist.bytecode.CodeAttribute
import javassist.bytecode.CodeIterator
import javassist.bytecode.ConstPool
import javassist.bytecode.Mnemonic
import javassist.bytecode.Opcode
import me.ryster.nheen.ir.Instruction

class JVMCodeGen(private val packageName: String) {
    private val pool = ClassPool.getDefault()
    private val variableMap = mutableMapOf<String, Int>()
    private var currentLocalIdx = 1

    private fun transformIrToByteCode(
        ir: List<Instruction>, constPool: ConstPool
    ): ByteArray {
        val src = mutableListOf<Int>()

        ir.forEach { instruction ->
            when (instruction) {
                is Instruction.Assign -> {
                    when (val newItem = instruction.value) {
                        is Instruction.SimpleValue.Inteiro -> {
                            src += CodeAttribute.LDC
                            src += constPool.addIntegerInfo(newItem.value)

                            src += CodeAttribute.ISTORE
                            src += currentLocalIdx
                            variableMap[instruction.variable] = currentLocalIdx++
                        }

                        is Instruction.SimpleValue.Texto -> {
                            src += CodeAttribute.LDC
                            src += constPool.addStringInfo(newItem.value)

                            src += CodeAttribute.ASTORE
                            src += currentLocalIdx
                            variableMap[instruction.variable] = currentLocalIdx++
                        }
                    }
                }

                is Instruction.Call -> {
                    // me.ryster.nheen.runtime.language
                    val console = constPool.addClassInfo("me/ryster/nheen/runtime/language/Console");
                    val methodIdx = constPool.addMethodrefInfo(
                        console,
                        instruction.function,
                        "(Ljava/lang/String;)V"
                    )


                    src += Opcode.INVOKESTATIC
                    src += (methodIdx shr 8)
                    src += methodIdx
                }

                is Instruction.PushValue -> {
                    when (val value = instruction.value) {
                        is Instruction.SimpleValue.Inteiro -> {
                            src += CodeAttribute.LDC
                            src += constPool.addIntegerInfo(value.value)
                        }

                        is Instruction.SimpleValue.Texto -> {
                            src += CodeAttribute.LDC
                            src += constPool.addStringInfo(value.value)
                        }
                    }
                }

                is Instruction.PushVariable -> {
                    val idx = variableMap[instruction.variable]!!
                    src += CodeAttribute.ALOAD
                    src += idx
                }

                Instruction.ReturnVoid -> {
                    src += CodeAttribute.RETURN
                }
            }
        }

        return src.map {
            it.toByte()
        }.toByteArray();
    }


    fun transformFromIR(ir: List<Instruction>, className: String): CtClass {
        val klass = pool.makeClass("$packageName.$className")

        val main = CtNewMethod.make(
            CtClass.voidType, "inicio",
            arrayOf(), null, null, klass
        )

        val methodInfo = main.methodInfo
        val constPool = methodInfo.constPool
        val src = transformIrToByteCode(ir, constPool)


        methodInfo.setSuperclass("java/lang/Object")
        constPool.classNames.add("java/lang/Object")
        constPool.classNames.add("me/ryster/nheen/runtime/language/Console")

        val codeAttr = CodeAttribute(
            constPool, 4096 * 2, currentLocalIdx,
            src, methodInfo.codeAttribute.exceptionTable
        )


        main.methodInfo.setCodeAttribute(codeAttr)

        klass.addMethod(main)

        return klass
    }
}
