package me.ryster.nheen.auxiliary

import me.ryster.nheen.runtime.core.ConsoleIO

class SimpleConsoleIO: ConsoleIO() {
    override fun writeText(texto: String) {
        println(texto)
    }

    override fun readText(): String {
        return readln()
    }

    override fun readNumber(): Int {
        return readln().toInt()
    }
}