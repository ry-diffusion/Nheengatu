package me.ryster.nheen

import me.ryster.nheen.auxiliary.SimpleConsoleIO
import me.ryster.nheen.auxiliary.inspectText
import me.ryster.nheen.runtime.language.consoleIO
import me.ryster.nheen.transformers.TreeToIRTransformer


fun main() {
    consoleIO = SimpleConsoleIO()
    val source = inspectText(
        """
         pacote Principal
         
         inicio
            texto: Texto = "Olá mundo!"
            imprima(texto)
         fim
        """.trimIndent()
    )

    println(
        source.text
    )

    val irTransformer = TreeToIRTransformer()

    irTransformer.transformTree(source)
    println("=== REPRESENTAÇÃO INTERMEDIARIA ==")
    for (inst in irTransformer.packages["Principal"]!!) {
        println(" > $inst")
    }
}