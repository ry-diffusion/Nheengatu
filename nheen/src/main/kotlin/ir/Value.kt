package me.ryster.nheen.ir


sealed class Value {
    data class Variable(val name: String) : Value()
    data class Raw(val literal: Literal) : Value()
    data class FunctionCall(val instruction: Instruction.Call) : Value()
}
