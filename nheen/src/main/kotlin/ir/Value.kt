package me.ryster.nheen.ir


sealed class Value {
    data class Variable(val name: String) : Value()
    data class Raw(val literal: me.ryster.nheen.ir.Literal) : Value()
}
