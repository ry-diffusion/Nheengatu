package me.ryster.nheen

import me.ryster.nheen.auxiliary.SimpleConsoleIO
import me.ryster.nheen.auxiliary.inspectText
import me.ryster.nheen.codegen.JvmCodeGen
import me.ryster.nheen.codegen.JvmIrTransformer
import me.ryster.nheen.runtime.NheenClassLoader
import me.ryster.nheen.runtime.language.io.Console
import me.ryster.nheen.transformers.TreeToIRTransformer
import java.io.File

fun main() {
    Console.setIO(SimpleConsoleIO())

    val source = inspectText(
        """
         pacote Principal
         
         inicio
            a := "Olá mundo!" 
            b := 2
            b := a
            a := 5
            a := b
            
            imprima("==== O valor de a === ")
            imprima(a)
            imprima("==== O valor de b === ")
            imprima(b)
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

    val codegen = JvmCodeGen("nheen.code")

    println("=== BYTECODE JVM ===")
    val jvmTransformer = JvmIrTransformer("nheen.code", "Principal", principal);
    val jvmIr = jvmTransformer.doTransform().onEach {
        println(" $it")
    }

    val principalJavaRepr = codegen.transform(jvmIr, "Principal", "inicio", jvmTransformer.usedVariables())
    val nheenClassLoader = NheenClassLoader()

    val f = File("test.class")
    val bytes = principalJavaRepr.toBytecode()
    f.writeBytes(bytes)
    println("Escrito em: ${f.absolutePath}")


    val principalClass = nheenClassLoader.loadClass("nheen.code.Principal", bytes)
    principalClass.getDeclaredMethod("inicio").invoke(principalClass.newInstance())

}