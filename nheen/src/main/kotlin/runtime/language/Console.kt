package me.ryster.nheen.runtime.language

import me.ryster.nheen.runtime.core.ConsoleIO

lateinit var consoleIO: ConsoleIO

class Console {
    companion object {
        fun imprima(texto: String) {
            consoleIO.writeText(texto)
        }

        fun ler_texto(): String {
            return consoleIO.readText()
        }

        fun ler_numero(): Int {
            return consoleIO.readNumber()
        }
    }
}