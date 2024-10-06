package com.llamalad7.classpathchecker

import org.objectweb.asm.ClassReader
import org.objectweb.asm.ClassWriter
import org.objectweb.asm.commons.ClassRemapper
import org.objectweb.asm.commons.Remapper
import java.io.InputStream
import java.util.jar.JarFile

class ClasspathModel(private val jars: List<JarFile>) {
    private val visiting = mutableSetOf<ClassName>()

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
            return
        }
        // A ClassRemapper is a convenient way to visit every class reference in the given class.
        // Unfortunately, it needs a backing visitor which provides all possible sub-visitors (MethodVisitor, etc).
        // We use a ClassWriter since that is likely to be the cheapest built-in implementation.
        // If performance is of particular concern we could instead provide dummy implementations for all visitor types.
        ClassReader(getClassStream(name)).accept(ClassRemapper(ClassWriter(0), ScanningRemapper()), 0)
    }

    private fun getClassStream(name: ClassName): InputStream {
        return jars.firstNotNullOfOrNull { jar ->
            jar.getEntry(name.fileName)?.let { jar.getInputStream(it) }
        } ?: throw MissingClassException
    }

    private inner class ScanningRemapper : Remapper() {
        override fun map(internalName: String): String {
            scanClass(ClassName(internalName))
            return super.map(internalName)
        }
    }
}