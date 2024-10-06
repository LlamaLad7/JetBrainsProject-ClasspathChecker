package com.llamalad7.classpathchecker

import java.util.jar.JarFile

fun main(args: Array<String>) {
    val mainClass = args.getOrNull(0) ?: error("Please pass a main class")
    args.asSequence().drop(1).map { JarFile(it) }.useAll { jars ->
        println(ClasspathModel(jars).canRun(ClassName(mainClass)))
    }
}