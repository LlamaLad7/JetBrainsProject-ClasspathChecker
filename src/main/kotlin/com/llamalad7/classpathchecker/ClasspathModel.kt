package com.llamalad7.classpathchecker

import org.objectweb.asm.ClassReader
import org.objectweb.asm.commons.ClassRemapper
import org.objectweb.asm.commons.Remapper
import java.io.InputStream
import java.util.jar.JarFile

class ClasspathModel(private val jars: List<JarFile>, private val verbose: Boolean) {
    private val seen = mutableSetOf<ClassName>()
    private val toVisit = ArrayDeque<ClassReader>()

    // A ClassRemapper is a convenient way to visit every class reference in the given class.
    // Unfortunately, it needs a backing visitor which provides all possible sub-visitors (MethodVisitor, etc).
    private val scanner = ClassRemapper(DummyClassVisitor, ScanningRemapper())

    fun canRun(mainClass: ClassName): Boolean {
        enqueueClass(mainClass)
        while (toVisit.isNotEmpty()) {
            val nextClass = toVisit.removeFirst()
            try {
                nextClass.accept(scanner, 0)
            } catch (_: MissingClassException) {
                toVisit.clear()
                return false
            }
        }
        return true
    }

    private fun enqueueClass(name: ClassName) {
        if (!seen.add(name)) {
            return
        }
        if (ClassLoader.getPlatformClassLoader().getResource(name.fileName) != null) {
            // This is a JDK class, don't bother scanning it, we can assume the JDK is complete
            log { "$name is present in current JDK" }
            return
        }
        toVisit.addLast(ClassReader(getClassStream(name)))
    }

    private fun getClassStream(name: ClassName): InputStream {
        val stream = jars.firstNotNullOfOrNull { jar ->
            jar.getEntry(name.fileName)?.let { jar.getInputStream(it) }
                ?.also { log { "$name is present in ${jar.name}" } }
        }
        if (stream == null) {
            log { "$name is missing" }
            throw MissingClassException
        }
        return stream
    }

    private inner class ScanningRemapper : Remapper() {
        override fun map(internalName: String): String {
            enqueueClass(ClassName(internalName))
            return super.map(internalName)
        }
    }

    private inline fun log(message: () -> String) {
        if (verbose) {
            println(message())
        }
    }
}