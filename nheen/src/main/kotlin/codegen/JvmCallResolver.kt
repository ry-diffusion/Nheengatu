package me.ryster.nheen.codegen

import me.ryster.nheen.ir.Operation
import me.ryster.nheen.runtime.language.core.RuntimeObject
import me.ryster.nheen.runtime.language.io.Console
import me.ryster.nheen.transformers.CallResolver
import java.lang.reflect.Method
import kotlin.reflect.typeOf

val Class<*>.descriptor: String
    get() = when (this) {
        java.lang.Void.TYPE -> "V"
        java.lang.Boolean.TYPE -> "Z"
        java.lang.Byte.TYPE -> "B"
        java.lang.Character.TYPE -> "C"
        java.lang.Short.TYPE -> "S"
        java.lang.Integer.TYPE -> "I"
        java.lang.Long.TYPE -> "J"
        java.lang.Float.TYPE -> "F"
        java.lang.Double.TYPE -> "D"
        else -> "L${this.name.replace('.', '/')};"
    }


fun getMethodSignature(method: Method): String {
    val returnType = method.returnType.descriptor
    val parameterTypes = method.parameterTypes.joinToString("") { it.descriptor }
    return "(${parameterTypes})${returnType}"
}


class JvmCallResolver
    (val packageName: String) : CallResolver() {
    class PreludeFunction(
        val className: Class<*>, val methodName: String, val protoArgs: List<Class<*>> = emptyList()
    ) {
        val prototype: String = getMethodSignature(className.getMethod(methodName, *protoArgs.toTypedArray()))
    }

    private val preludeFunctionMapping = mapOf(
        "imprima" to PreludeFunction(
            Console::class.java, "imprima", listOf(RuntimeObject::class.java)
        ),

        "lerInteiro" to PreludeFunction(
            Console::class.java, "lerInteiro", listOf()
        )
    )

    val operationsPreludeMapping = mapOf(
        Operation.Plus::class to PreludeFunction(
            RuntimeObject::class.java, "plus",

            listOf(RuntimeObject::class.java)
        ),

        Operation.Minus::class to PreludeFunction(
            RuntimeObject::class.java, "minus", listOf(RuntimeObject::class.java)
        ),

        Operation.Multiply::class to PreludeFunction(
            RuntimeObject::class.java, "times",
            listOf(RuntimeObject::class.java)
        ),

        Operation.Divide::class to PreludeFunction(
            RuntimeObject::class.java, "div", listOf(RuntimeObject::class.java)
        )
    )


    fun resolve(name: String): JvmIr {
        if (preludeFunctionMapping.containsKey(name)) {
            val function = preludeFunctionMapping[name]!!
            return JvmIr.StaticCall(
                function.className.name, function.methodName, function.prototype, emptyList()
            )
        }

        TODO("Função não implementada! ${name}")
    }


    fun resolveOperation(operation: Operation): JvmIr {
        if (operationsPreludeMapping.containsKey(operation::class)) {
            val function = operationsPreludeMapping[operation::class]!!
            return JvmIr.InvokeVirtual(
                function.className.name, function.methodName, function.prototype, emptyList()
            )
        }

        TODO("Operator não implementado: $operation")
    }
}