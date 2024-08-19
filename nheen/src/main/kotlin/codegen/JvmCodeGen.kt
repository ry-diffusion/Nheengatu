package me.ryster.nheen.codegen

import me.ryster.nheen.runtime.language.core.RuntimeObject
import org.objectweb.asm.ClassWriter
import org.objectweb.asm.Label
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes
import org.objectweb.asm.Type


class JvmCodeGen(private val packageName: String) : Opcodes {
    private val labels = mutableMapOf<String, Label>()

    private fun addDefaultConstructor(classWriter: ClassWriter) {
        // Create a public constructor <init>()
        val constructorVisitor = classWriter.visitMethod(
            Opcodes.ACC_PUBLIC, // Access modifier: public
            "<init>",           // Constructor name
            "()V",              // Descriptor: no arguments, void return type
            null,               // Signature: null for a simple constructor
            null                // Exceptions: null if no exceptions are thrown
        )

        constructorVisitor.visitCode()

        // Call the superclass constructor (e.g., java.lang.Object.<init>())
        constructorVisitor.visitVarInsn(Opcodes.ALOAD, 0) // Load "this" onto the stack
        constructorVisitor.visitMethodInsn(
            Opcodes.INVOKESPECIAL,   // Invoke special method (constructor)
            "java/lang/Object",      // Superclass name (Object)
            "<init>",                // Constructor name
            "()V",                   // Descriptor of the superclass constructor
            false                    // Is it an interface method? No.
        )

        // End the constructor with a return statement
        constructorVisitor.visitInsn(Opcodes.RETURN)

        // Specify the maximum stack and local variables
        constructorVisitor.visitMaxs(1, 1)

        // End the method
        constructorVisitor.visitEnd()
    }

    private fun transformIrToByteCode(
        ir: List<JvmIr>, mv: MethodVisitor
    ) {
        ir.forEach {
            when (it) {
                is JvmIr.InvokeSpecial -> {
                    mv.visitMethodInsn(
                        Opcodes.INVOKESPECIAL,
                        it.className.replace('.', '/'),
                        it.methodName,
                        it.prototype,
                        false
                    )
                }

                is JvmIr.Goto -> {
                    mv.visitJumpInsn(Opcodes.GOTO, labels[it.label])
                }

                is JvmIr.InvokeVirtual -> {
                    mv.visitMethodInsn(
                        Opcodes.INVOKEVIRTUAL,
                        it.className.replace('.', '/'),
                        it.methodName,
                        it.prototype,
                        false
                    )
                }

                is JvmIr.NewObject -> {
                    mv.visitTypeInsn(Opcodes.NEW, it.classRef.name.replace('.', '/'))
                    mv.visitInsn(Opcodes.DUP)
                }

                is JvmIr.PushInt -> {
                    mv.visitLdcInsn(it.value)
                }

                is JvmIr.PushString -> {
                    mv.visitLdcInsn(it.value)
                }

                JvmIr.ReturnVoid -> {
                    mv.visitInsn(Opcodes.RETURN)
                }

                is JvmIr.StaticCall -> {
                    mv.visitMethodInsn(
                        Opcodes.INVOKESTATIC,
                        it.className.replace('.', '/'),
                        it.methodName,
                        it.prototype,
                        false
                    )
                }

                is JvmIr.StoreReference -> {
                    mv.visitVarInsn(Opcodes.ASTORE, it.variableIdx)
                }

                is JvmIr.LoadReference -> {
                    mv.visitVarInsn(Opcodes.ALOAD, it.variableIdx)
                }

                is JvmIr.IfEq -> {
                    val runtimeObject = Type.getInternalName(RuntimeObject::class.java)
                    mv.visitMethodInsn(
                        Opcodes.INVOKEVIRTUAL,
                        runtimeObject,
                        "toBoolean",
                        "()Z",
                        false
                    )
                    mv.visitJumpInsn(Opcodes.IFEQ, labels[it.otherwiseLabel])
                    mv.visitJumpInsn(Opcodes.GOTO, labels[it.thenLabel])
                }

                is JvmIr.DeclareLabel -> {
                    labels[it.label] = Label()
                }

                is JvmIr.BeginLabel -> {
                    mv.visitLabel(labels[it.label])
                }
            }
        }

    }

    fun transform(ir: List<JvmIr>, className: String, functionName: String, countVariables: Int): ByteArray {
        val classWriter = ClassWriter(ClassWriter.COMPUTE_FRAMES or ClassWriter.COMPUTE_MAXS)
        val fullClassName = "$packageName/$className".replace('.', '/')
        classWriter.visit(Opcodes.V1_8, Opcodes.ACC_PUBLIC, fullClassName, null, "java/lang/Object", null)

        // Add the default constructor
        addDefaultConstructor(classWriter)

        // Add the main method
        val mv = classWriter.visitMethod(
            Opcodes.ACC_PUBLIC or Opcodes.ACC_STATIC,
            functionName,
            "()V",
            null,
            null
        )
        mv.visitCode()

        // Transform IR to Bytecode
        transformIrToByteCode(ir, mv)

        mv.visitMaxs(0, countVariables)
        mv.visitEnd()

        classWriter.visitEnd()
        return classWriter.toByteArray()
    }
}
