package com.llamalad7.classpathchecker.tests

import java.io.ByteArrayOutputStream
import java.io.PrintStream

inline fun captureStdout(block: () -> Unit): String {
    val stdout = ByteArrayOutputStream()
    val prev = System.out
    System.setOut(PrintStream(stdout))
    try {
        block()
        return stdout.toString().trimEnd()
    } finally {
        System.setOut(prev)
    }
}