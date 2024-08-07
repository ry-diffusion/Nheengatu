package me.ryster.nheen.runtime.language.core

import me.ryster.nheen.runtime.language.core.errors.UnsupportedOperation

abstract class RuntimeObject {
    abstract fun representation(): String

    abstract val id: String

    open fun plus(other: RuntimeObject): RuntimeObject {
        throw UnsupportedOperation()
    }

    open fun minus(other: RuntimeObject): RuntimeObject {
        throw UnsupportedOperation()
    }

    open fun times(other: RuntimeObject): RuntimeObject {
        throw UnsupportedOperation()
    }

    open fun div(other: RuntimeObject): RuntimeObject
    {
        throw UnsupportedOperation()
    }

    open fun mod(other: RuntimeObject): RuntimeObject {
        throw UnsupportedOperation()
    }

    open fun and(other: RuntimeObject): RuntimeObject {
        throw UnsupportedOperation()
    }

    open fun or(other: RuntimeObject): RuntimeObject {
        throw UnsupportedOperation()
    }

    open fun not(): RuntimeObject {
        throw UnsupportedOperation()

    }

    open fun eq(other: RuntimeObject): Boolean {
        throw UnsupportedOperation()
    }

    open fun lessThan(other: RuntimeObject): Boolean {
        throw UnsupportedOperation()
    }

    open fun greaterThan(other: RuntimeObject): Boolean {
        throw UnsupportedOperation()
    }

    open fun lessThanOrEqual(other: RuntimeObject): Boolean {
        throw UnsupportedOperation()
    }


    open fun greaterThanOrEqual(other: RuntimeObject): Boolean {
        throw UnsupportedOperation()
    }
}