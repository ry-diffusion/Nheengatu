plugins {
    kotlin("jvm")
    antlr
}

group = "me.ryster"
version = "unspecified"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))
    antlr("org.antlr:antlr4:4.9") // use ANTLR version 4
}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(21)
}

tasks.generateGrammarSource {
    outputDirectory = file("${project.buildDir}/generated/sources/main/java/antlr")

    arguments = arguments + listOf("-visitor", "-long-messages") + listOf("-package", "me.ryster.nheen.grammar")
}

sourceSets {
    main {
        java {
            srcDir(tasks.generateGrammarSource)
        }
    }
}