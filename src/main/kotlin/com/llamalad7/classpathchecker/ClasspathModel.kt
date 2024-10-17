package com.llamalad7.classpathchecker

import org.objectweb.asm.ClassReader
import org.objectweb.asm.commons.ClassRemapper
import org.objectweb.asm.commons.Remapper
import java.io.InputStream
import java.util.jar.JarFile

class ClasspathModel(private val jars: List<JarFile>, private val verbose: Boolean) {
    private val visiting = mutableSetOf<ClassName>()

    // A ClassRemapper is a convenient way to visit every class reference in the given class.
    // Unfortunately, it needs a backing visitor which provides all possible sub-visitors (MethodVisitor, etc).
    private val scanner = ClassRemapper(DummyClassVisitor, ScanningRemapper())

    fun canRun(mainClass: ClassName): Boolean {
        try {
            scanClass(mainClass)
            return true
        } catch (_: MissingClassException) {
            return false
        }
    }

    private fun scanClass(name: ClassName) {
        if (!visiting.add(name)) {
            return
        }
        if (ClassLoader.getPlatformClassLoader().getResource(name.fileName) != null) {
            // This is a JDK class, don't bother scanning it, we can assume the JDK is complete
            log { "$name is built-in" }
            return
        }
        ClassReader(getClassStream(name)).accept(scanner, 0)
    }

    private fun getClassStream(name: ClassName): InputStream {
        val stream = jars.firstNotNullOfOrNull { jar ->
            jar.getEntry(name.fileName)?.let { jar.getInputStream(it) }
        }
        if (stream == null) {
            log { "$name is missing" }
            throw MissingClassException
        }
        log { "$name is present" }
        return stream
    }

    private inner class ScanningRemapper : Remapper() {
        override fun map(internalName: String): String {
            scanClass(ClassName(internalName))
            return super.map(internalName)
        }
    }

    private inline fun log(message: () -> String) {
        if (verbose) {
            println(message())
        }
    }
}