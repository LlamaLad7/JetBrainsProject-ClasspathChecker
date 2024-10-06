package com.llamalad7.classpathchecker

inline fun <T : AutoCloseable, R> Sequence<T>.useAll(routine: (List<T>) -> R): R {
    val toClose = mutableListOf<T>()
    try {
        forEach { toClose.add(it) }
        return routine(toClose)
    } finally {
        toClose.asReversed().forEach { it.close() }
    }
}

class ClassName(name: String) {
    val binaryName = name.replace('/', '.')

    val internalName get() = binaryName.replace('.', '/')

    val fileName get() = "$internalName.class"

    override fun toString() = binaryName

    override fun equals(other: Any?) = binaryName == (other as? ClassName)?.binaryName

    override fun hashCode() = binaryName.hashCode()
}