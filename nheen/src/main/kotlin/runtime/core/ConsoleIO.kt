package me.ryster.nheen.runtime.core

abstract class ConsoleIO {
    abstract fun writeText(texto: String)
    abstract fun readText(): String
    abstract fun readNumber(): Int
}