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
import java.lang.reflect.Constructor

fun main() {
    Console.setIO(SimpleConsoleIO())

    val source = inspectText(
        """
         pacote Principal
         
         inicio
            escreva("Digite um número: ")
            n := lerInteiro()  
            
            se n > 2 então 
               imprima("Esse número é maior que dois!")
             senão
               imprima("Esse número é menor que dois!")
            fim
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
    val bytes = principalJavaRepr
    f.writeBytes(bytes)
    println("Escrito em: ${f.absolutePath}")


    val principalClass = nheenClassLoader.loadClass("nheen.code.Principal", bytes)
    val instance = principalClass.getDeclaredConstructor().newInstance()
    val inicio = principalClass.getDeclaredMethod("inicio")

    inicio.invoke(instance)
}