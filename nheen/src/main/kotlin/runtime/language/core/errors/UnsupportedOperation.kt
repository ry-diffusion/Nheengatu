package me.ryster.nheen.runtime.language.core.errors

class UnsupportedOperation(
    private val operation: String =
        "Essa operação"
) : Throwable() {

    override fun toString(): String {
        return "$operation não é suportada."
    }
}