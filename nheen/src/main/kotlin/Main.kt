package me.ryster.nheen

import me.ryster.nheen.auxiliary.SimpleConsoleIO
import me.ryster.nheen.auxiliary.inspectText
import me.ryster.nheen.codegen.JvmCallResolver
import me.ryster.nheen.codegen.JvmCodeGen
import me.ryster.nheen.codegen.JvmIrTransformer
import me.ryster.nheen.runtime.NheenClassLoader
import me.ryster.nheen.runtime.language.io.Console
import me.ryster.nheen.visitor.TreeToIRVisitor
import java.io.File

fun main() {
    Console.setIO(SimpleConsoleIO())

    val source = inspectText(
        """
         pacote Principal
         
         inicio
            x := 2 + 2 + 2
            
            imprima("O valor de x é: " + x)
            
            imprima("*")
            imprima("*"*2)
            imprima("*"*10)
         fim
        """.trimIndent()
    )

    println(
        source.text
    )

    val visitor = TreeToIRVisitor()
    visitor.visit(source)

    val principal = visitor.getInstructions()

    println("=== REPRESENTAÇÃO INTERMEDIARIA ==")
    for (inst in principal) {
        println(" > $inst")
    }

    val codegen = JvmCodeGen("nheen.code")

    println("=== BYTECODE JVM ===")

    val resolver = JvmCallResolver("Principal")
    val jvmTransformer = JvmIrTransformer(resolver, principal);
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