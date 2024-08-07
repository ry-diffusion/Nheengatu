package me.ryster.nheen.runtime.language.core.objects

import me.ryster.nheen.runtime.language.core.RuntimeObject
import me.ryster.nheen.runtime.language.core.errors.UnsupportedOperation

class TextoObject(val value: String) : RuntimeObject() {
    override val id = "Texto"

    override fun representation(): String {
        return value
    }

    override fun plus(other: RuntimeObject): RuntimeObject {
        return TextoObject(value + other.representation())
    }

    override fun times(other: RuntimeObject): RuntimeObject {
        if (other !is InteiroObject) {
            throw UnsupportedOperation()
        }

        return TextoObject(value.repeat(other.value))
    }

    override fun eq(other: RuntimeObject): Boolean {
        if (other !is TextoObject) {
            return false
        }

        return value == other.value
    }
}