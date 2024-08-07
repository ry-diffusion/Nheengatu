package me.ryster.nheen.ir

sealed class Literal {
    data class Inteiro(val value: Int) : Literal()
    data class Texto(val value: String) : Literal()
}
