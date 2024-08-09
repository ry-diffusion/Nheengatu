package me.ryster.nheen.ir

sealed class Instruction {
    data class Assign(
        val variable: String,
        val value: Value,
    ) : Instruction()

    data class Call(
        val originPackage: String,
        val function: String,
        val arguments: List<Value>,
    ) : Instruction()

    data object ReturnVoid : Instruction()
}

private val helloWorldCode = listOf(
    Instruction.Assign("x", Value.Raw(Literal.Inteiro(2))),
    Instruction.Call(
        "Principal", "imprima",
        listOf(Value.Variable("x"))
    ),
    Instruction.ReturnVoid
)
