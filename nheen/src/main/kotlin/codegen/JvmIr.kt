package me.ryster.nheen.codegen

sealed class JvmIr {
    data class NewObject(
        val classRef: Class<*>
    ) : JvmIr()

    data class PushInt(
        val value: Int
    ) : JvmIr()

    data class PushString(
        val value: String
    ) : JvmIr()

    data class InvokeSpecial(
        val className: String,
        val methodName: String,
        val prototype: String,
        val arguments: List<JvmIr>
    ) : JvmIr()

    data class InvokeVirtual(
        val className: String,
        val methodName: String,
        val prototype: String,
        val arguments: List<JvmIr>
    ) : JvmIr()

    data class StoreReference(
        val variableIdx: Int
    ) : JvmIr()

    data class LoadReference(
        val variableIdx: Int
    ) : JvmIr()

    data class StaticCall(
        val className: String,
        val methodName: String,
        val prototype: String,
        val arguments: List<JvmIr>
    ) : JvmIr()

    data object ReturnVoid : JvmIr()
}