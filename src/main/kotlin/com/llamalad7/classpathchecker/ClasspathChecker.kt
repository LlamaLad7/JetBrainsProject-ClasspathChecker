package com.llamalad7.classpathchecker

import com.github.ajalt.clikt.core.CoreCliktCommand
import com.github.ajalt.clikt.core.main
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.arguments.help
import com.github.ajalt.clikt.parameters.arguments.multiple
import com.github.ajalt.clikt.parameters.options.flag
import com.github.ajalt.clikt.parameters.options.help
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.types.file
import java.io.File
import java.util.jar.JarFile

class ClasspathChecker : CoreCliktCommand("java -jar classpath-checker.jar") {
    override val printHelpOnEmptyArgs: Boolean get() = true

    private val mainClass: String by argument()
        .help { "Fully-qualified main class name" }

    private val jars: List<File> by argument()
        .file(mustExist = true, canBeDir = false, mustBeReadable = true)
        .multiple()
        .help { "JAR files which make up the classpath you want to check" }

    private val verbose: Boolean by option()
        .flag()
        .help { "Enables detailed logging of all considered classes" }

    override fun run() {
        jars.asSequence().map { JarFile(it) }.useAll { jarFiles ->
            val model = ClasspathModel(jarFiles, verbose)
            println(model.canRun(ClassName(mainClass)))
        }
    }
}

fun main(args: Array<String>) {
    ClasspathChecker().main(args)
}