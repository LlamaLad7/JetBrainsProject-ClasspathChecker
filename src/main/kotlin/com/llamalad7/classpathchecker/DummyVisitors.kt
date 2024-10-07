package com.llamalad7.classpathchecker

import org.objectweb.asm.AnnotationVisitor
import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.FieldVisitor
import org.objectweb.asm.Label
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.ModuleVisitor
import org.objectweb.asm.RecordComponentVisitor
import org.objectweb.asm.TypePath

// Serves as a backing visitor for a `ClassRemapper` for when we just want to visit without outputting.
object DummyClassVisitor : ClassVisitor(ASM_VERSION) {
    override fun visitModule(name: String?, access: Int, version: String?): ModuleVisitor = DummyModuleVisitor

    override fun visitAnnotation(descriptor: String?, visible: Boolean): AnnotationVisitor = DummyAnnotationVisitor

    override fun visitTypeAnnotation(
        typeRef: Int,
        typePath: TypePath?,
        descriptor: String?,
        visible: Boolean
    ): AnnotationVisitor =
        DummyAnnotationVisitor

    override fun visitRecordComponent(name: String?, descriptor: String?, signature: String?): RecordComponentVisitor =
        DummyRecordComponentVisitor

    override fun visitField(
        access: Int,
        name: String?,
        descriptor: String?,
        signature: String?,
        value: Any?
    ): FieldVisitor =
        DummyFieldVisitor

    override fun visitMethod(
        access: Int,
        name: String?,
        descriptor: String?,
        signature: String?,
        exceptions: Array<out String>?
    ): MethodVisitor = DummyMethodVisitor
}

private object DummyModuleVisitor : ModuleVisitor(ASM_VERSION)

private object DummyAnnotationVisitor : AnnotationVisitor(ASM_VERSION) {
    override fun visitAnnotation(name: String?, descriptor: String?): AnnotationVisitor = this

    override fun visitArray(name: String?): AnnotationVisitor = this
}

private object DummyRecordComponentVisitor : RecordComponentVisitor(ASM_VERSION) {
    override fun visitAnnotation(descriptor: String?, visible: Boolean): AnnotationVisitor = DummyAnnotationVisitor

    override fun visitTypeAnnotation(
        typeRef: Int,
        typePath: TypePath?,
        descriptor: String?,
        visible: Boolean
    ): AnnotationVisitor =
        DummyAnnotationVisitor
}

private object DummyFieldVisitor : FieldVisitor(ASM_VERSION) {
    override fun visitAnnotation(descriptor: String?, visible: Boolean): AnnotationVisitor = DummyAnnotationVisitor

    override fun visitTypeAnnotation(
        typeRef: Int,
        typePath: TypePath?,
        descriptor: String?,
        visible: Boolean
    ): AnnotationVisitor =
        DummyAnnotationVisitor
}

private object DummyMethodVisitor : MethodVisitor(ASM_VERSION) {
    override fun visitAnnotation(descriptor: String?, visible: Boolean): AnnotationVisitor = DummyAnnotationVisitor

    override fun visitTypeAnnotation(
        typeRef: Int,
        typePath: TypePath?,
        descriptor: String?,
        visible: Boolean
    ): AnnotationVisitor =
        DummyAnnotationVisitor

    override fun visitParameterAnnotation(parameter: Int, descriptor: String?, visible: Boolean): AnnotationVisitor =
        DummyAnnotationVisitor

    override fun visitInsnAnnotation(
        typeRef: Int,
        typePath: TypePath?,
        descriptor: String?,
        visible: Boolean
    ): AnnotationVisitor =
        DummyAnnotationVisitor

    override fun visitTryCatchAnnotation(
        typeRef: Int,
        typePath: TypePath?,
        descriptor: String?,
        visible: Boolean
    ): AnnotationVisitor =
        DummyAnnotationVisitor

    override fun visitLocalVariableAnnotation(
        typeRef: Int,
        typePath: TypePath?,
        start: Array<out Label>?,
        end: Array<out Label>?,
        index: IntArray?,
        descriptor: String?,
        visible: Boolean
    ): AnnotationVisitor = DummyAnnotationVisitor
}