package me.ryster

import me.ryster.nheen.grammar.NheenLexer
import me.ryster.nheen.grammar.NheenParser
import org.antlr.v4.runtime.CharStream
import org.antlr.v4.runtime.CommonTokenStream
import org.antlr.v4.runtime.misc.Interval
import org.antlr.v4.runtime.CharStreams


fun inspectTextToTree(code: String): String {
    val lexer = NheenLexer(
        CharStreams.fromString(code)
    )
    val tokens = CommonTokenStream(lexer)
    val parser = NheenParser(tokens)
    val inicio = parser.inicio()
    return inicio.toStringTree();
}

fun main() {
    println(
        inspectTextToTree(
            """
         inicio
            x: Inteiro = 2
            imprima("Olá mundo!")
         fim
        """.trimIndent()
        ))

}