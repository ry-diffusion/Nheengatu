package me.ryster.nheen.ir

sealed class Operation {
    abstract val left: Value
    abstract val right: Value

    data class Plus(
        override val left: Value, override val right: Value
    ) : Operation()

    data class Minus(
        override val left: Value, override val right: Value
    ) : Operation()

    data class Multiply(
        override val left: Value,
        override val right: Value,
    ) : Operation()

    data class Divide(
        override val left: Value,
        override val right: Value,
    ) : Operation()
}