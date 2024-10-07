plugins {
    kotlin("jvm") version "2.0.20"
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