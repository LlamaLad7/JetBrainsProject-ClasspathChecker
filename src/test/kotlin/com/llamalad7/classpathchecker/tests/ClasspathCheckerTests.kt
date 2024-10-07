package com.llamalad7.classpathchecker.tests

import com.llamalad7.classpathchecker.main
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import kotlin.test.assertEquals

class ClasspathCheckerTests {
    @ParameterizedTest
    @MethodSource("testCases")
    fun test(args: Array<String>, expected: Boolean) {
        val result = captureStdout {
            main(args)
        }
        assertEquals(expected.toString(), result)
    }

    companion object {
        @JvmStatic
        private fun testCases() =
            listOf(
                testCase(false, "com.jetbrains.internship2024.ClassB", "ModuleB-1.0.jar"),
                testCase(true, "com.jetbrains.internship2024.ClassB", "ModuleA-1.0.jar", "ModuleB-1.0.jar"),
                testCase(true, "com.jetbrains.internship2024.ClassA", "ModuleA-1.0.jar"),
                testCase(false, "com.jetbrains.internship2024.SomeAnotherClass", "ModuleA-1.0.jar"),
                testCase(
                    true,
                    "com.jetbrains.internship2024.SomeAnotherClass",
                    "ModuleA-1.0.jar",
                    "commons-io-2.16.1.jar"
                ),
                testCase(true, "com.jetbrains.internship2024.ClassB1", "ModuleB-1.0.jar")
            )

        private fun testCase(expected: Boolean, vararg args: String) = Arguments.of(args, expected)
    }
}