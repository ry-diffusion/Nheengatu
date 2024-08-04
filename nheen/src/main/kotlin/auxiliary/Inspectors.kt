package me.ryster.nheen.auxiliary

import me.ryster.nheen.grammar.NheenLexer
import me.ryster.nheen.grammar.NheenParser
import org.antlr.v4.runtime.CharStreams
import org.antlr.v4.runtime.CommonTokenStream


fun inspectText(code: String): NheenParser.FileContext {
    val lexer = NheenLexer(
        CharStreams.fromString(code)
    )
    val tokens = CommonTokenStream(lexer)
    val parser = NheenParser(tokens)
    return parser.file()
}