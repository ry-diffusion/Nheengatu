package me.ryster.nheen.runtime.language.io

import me.ryster.nheen.runtime.core.ConsoleIO
import me.ryster.nheen.runtime.language.core.RuntimeObject
import me.ryster.nheen.runtime.language.core.objects.InteiroObject
import me.ryster.nheen.runtime.language.core.objects.TextoObject


class Console {
    companion object {
        private lateinit var consoleIO: ConsoleIO

        fun setIO(io: ConsoleIO) {
            consoleIO = io
        }

        @JvmStatic
        fun imprima(texto: RuntimeObject) {
            consoleIO.writeText("${texto.representation()}\n")
        }

        @JvmStatic
        fun escreva(texto: RuntimeObject) {
            consoleIO.writeText(texto.representation())
        }

        @JvmStatic
        fun lerTexto(): RuntimeObject = TextoObject(consoleIO.readText())

        @JvmStatic
        fun lerInteiro(): RuntimeObject =
            InteiroObject(consoleIO.readNumber())
    }
}