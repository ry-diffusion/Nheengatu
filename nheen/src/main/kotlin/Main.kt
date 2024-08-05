package me.ryster.nheen

import me.ryster.nheen.auxiliary.SimpleConsoleIO
import me.ryster.nheen.auxiliary.inspectText
import me.ryster.nheen.codegen.JVMCodeGen
import me.ryster.nheen.runtime.NheenClassLoader
import me.ryster.nheen.runtime.language.consoleIO
import me.ryster.nheen.transformers.TreeToIRTransformer
import java.io.File

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

    val principal = irTransformer.packages["Principal"]!!

    println("=== REPRESENTAÇÃO INTERMEDIARIA ==")
    for (inst in principal) {
        println(" > $inst")
    }

    val codegen = JVMCodeGen("nheen.code")
    val principalJavaRepr = codegen.transformFromIR(principal, "Principal")
    val nheenClassLoader = NheenClassLoader()

    val f = File("test.class")
    val bytes = principalJavaRepr.toBytecode()
    f.writeBytes(bytes)
    println("Escrito em: ${f.absolutePath}")


    val principalClass = nheenClassLoader.loadClass("nheen.code.Principal", bytes)
    principalClass.getDeclaredMethod("inicio").invoke(principalClass.newInstance())

}