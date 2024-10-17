plugins {
    kotlin("jvm") version "2.0.20"
    id("com.gradleup.shadow") version "8.3.3"
    application
}

group = "com.llamalad7"
version = "1.0-SNAPSHOT"

val testDataBuildDir by extra {
    project(":test-data").layout.buildDirectory.dir("libs")
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("com.github.ajalt.clikt:clikt-core:5.0.1")
    implementation("org.ow2.asm:asm-commons:9.7.1")
    testImplementation(kotlin("test"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

tasks.test {
    dependsOn(":test-data:build")
    useJUnitPlatform()
    workingDir = testDataBuildDir.get().asFile
}

kotlin {
    jvmToolchain(11)
}

application {
    mainClass = "com.llamalad7.classpathchecker.ClasspathCheckerKt"
}

tasks.jar {
    manifest {
        attributes["Main-Class"] = application.mainClass
    }
}