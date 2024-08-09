package me.ryster.nheen.ir

sealed class Operation {
    data class Plus(
        val left: Value,
        val right: Value,
    ) : Operation()

    data class Minus(
        val left: Value,
        val right: Value,
    ) : Operation()

    data class Multiply(
        val left: Value,
        val right: Value,
    ) : Operation()

    data class Divide(
        val left: Value,
        val right: Value,
    ) : Operation()
}