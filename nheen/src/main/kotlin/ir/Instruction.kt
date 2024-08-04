package me.ryster.nheen.ir

sealed class Instruction {
    sealed class Register {
        data class Variable(val index: Int) : Register()
        data class Param(val index: Int) : Register()
    }

    sealed class SimpleValue {
        data class Inteiro(val value: Int) : SimpleValue()
        data class Texto(val value: String) : SimpleValue()
    }

    data class Assign (
        val variable: String,
        val value: SimpleValue,
    ) : Instruction()


    data class PushVariable(
        val register: Register,
        val variable: String,
    ) : Instruction()

    data class PushValue(
        val register: Register,
        val value: SimpleValue,
    ) : Instruction()

    data class Call(
        val function: String,
        val arguments: List<Register>,
    ) : Instruction()

    data object ReturnVoid: Instruction()
}

private val helloWorld = listOf(
    Instruction.Assign("x", Instruction.SimpleValue.Inteiro(2)),
    Instruction.PushVariable(Instruction.Register.Variable(0), "x"),
    Instruction.Call("imprima", listOf(Instruction.Register.Variable(0))),
    Instruction.ReturnVoid
)
