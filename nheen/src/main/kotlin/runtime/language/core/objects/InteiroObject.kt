package me.ryster.nheen.runtime.language.core.objects

import me.ryster.nheen.runtime.language.core.RuntimeObject
import me.ryster.nheen.runtime.language.core.errors.UnsupportedOperation

class InteiroObject(val value: Int) : RuntimeObject() {
    override fun representation(): String {
        return value.toString()
    }

    override val id = "Inteiro"

    override fun plus(other: RuntimeObject): RuntimeObject {
        return when (other) {
            is InteiroObject -> {
                InteiroObject(value + other.value)
            }

            is TextoObject -> {
                TextoObject(value.toString() + other.value)
            }

            else -> {
                throw UnsupportedOperation()
            }
        }
    }

    override fun minus(other: RuntimeObject): RuntimeObject {
        if (other !is InteiroObject) {
            throw UnsupportedOperation()
        }

        return InteiroObject(value - other.value)
    }

    override fun toBoolean(): Boolean = value != 0

    override fun times(other: RuntimeObject): RuntimeObject {
        if (other !is InteiroObject) {
            throw UnsupportedOperation()
        }

        return InteiroObject(value * other.value)
    }

    override fun div(other: RuntimeObject): RuntimeObject {
        if (other !is InteiroObject) {
            throw UnsupportedOperation()
        }

        return InteiroObject(value / other.value)
    }

    override fun mod(other: RuntimeObject): RuntimeObject {
        if (other !is InteiroObject) {
            throw UnsupportedOperation()
        }

        return InteiroObject(value % other.value)
    }

    override fun and(other: RuntimeObject): RuntimeObject {
        if (other !is InteiroObject) {
            throw UnsupportedOperation()
        }

        return InteiroObject(value and other.value)
    }

    override fun or(other: RuntimeObject): RuntimeObject {
        if (other !is InteiroObject) {
            throw UnsupportedOperation()
        }

        return InteiroObject(value or other.value)
    }

    override fun not(): RuntimeObject {
        return InteiroObject(value.inv())
    }

    override fun greaterThanOrEqual(other: RuntimeObject): RuntimeObject {
        if (other !is InteiroObject) {
            return LogicoObject(false)
        }

        return LogicoObject(value >= other.value)
    }

    override fun eq(other: RuntimeObject): RuntimeObject {
        if (other !is InteiroObject) {
            return LogicoObject(false)
        }

        return LogicoObject(value == other.value)
    }

    override fun greaterThan(other: RuntimeObject): RuntimeObject {
        if (other !is InteiroObject) {
            return LogicoObject(false)
        }

        return LogicoObject(value >= other.value)
    }

    override fun lessThan(other: RuntimeObject): RuntimeObject {
        if (other !is InteiroObject) {
            return LogicoObject(false)
        }

        return LogicoObject(value < other.value)
    }
}