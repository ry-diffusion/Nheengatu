package me.ryster.nheen.runtime.language.core.objects

import me.ryster.nheen.runtime.language.core.RuntimeObject
import me.ryster.nheen.runtime.language.core.errors.UnsupportedOperation

class InteiroObject(val value: Int) : RuntimeObject() {
    override fun representation(): String {
        return value.toString()
    }

    override val id = "Inteiro"

    override fun plus(other: RuntimeObject): RuntimeObject {
        if (other !is InteiroObject) {
            throw UnsupportedOperation()
        }

        return InteiroObject(value + other.value)
    }

    override fun minus(other: RuntimeObject): RuntimeObject {
        if (other !is InteiroObject) {
            throw UnsupportedOperation()
        }

        return InteiroObject(value - other.value)
    }


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

    override fun eq(other: RuntimeObject): Boolean {
        if (other !is InteiroObject) {
            return false
        }

        return value == other.value
    }

    override fun lessThan(other: RuntimeObject): Boolean {
        if (other !is InteiroObject) {
            return false
        }

        return value < other.value
    }

    override fun greaterThan(other: RuntimeObject): Boolean {
        if (other !is InteiroObject) {
            return false
        }

        return value > other.value
    }

    override fun lessThanOrEqual(other: RuntimeObject): Boolean {
        if (other !is InteiroObject) {
            return false
        }

        return value <= other.value
    }

    override fun greaterThanOrEqual(other: RuntimeObject): Boolean {
        if (other !is InteiroObject) {
            return false
        }

        return value >= other.value
    }
}