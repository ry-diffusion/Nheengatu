package me.ryster.nheen

import me.ryster.nheen.grammar.NheenLexer
import me.ryster.nheen.grammar.NheenParser
import me.ryster.nheen.runtime.language.consoleIO
import org.antlr.v4.runtime.CharStream
import org.antlr.v4.runtime.CommonTokenStream
import org.antlr.v4.runtime.misc.Interval
import org.antlr.v4.runtime.CharStreams


fun inspectText(code: String): NheenParser.FileContext {
    val lexer = NheenLexer(
        CharStreams.fromString(code)
    )
    val tokens = CommonTokenStream(lexer)
    val parser = NheenParser(tokens)
    return parser.file()
}

fun main() {
    consoleIO = me.ryster.nheen.auxiliar.SimpleConsoleIO()

    println(
        inspectText(
            """
         pacote Principal
         
         inicio
            x: Inteiro = 2
            imprima("Olá mundo!")
         fim
        """.trimIndent()
        ).toStringTree()
    )

}