package com.llamalad7.classpathchecker

object MissingClassException : Throwable() {
    override fun fillInStackTrace() = this
}