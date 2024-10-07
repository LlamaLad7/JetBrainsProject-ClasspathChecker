group = "com.jetbrains.internship2024"
version = "1.0"

val testDataBuildDir: Provider<Directory> by rootProject.extra

subprojects {
    apply(plugin = "java")

    val copyIo by tasks.registering {
        val compileClasspath =
            project.configurations.matching { it.name == "compileClasspath" }
        compileClasspath.all {
            for (dep in map { file: File -> file.absoluteFile }) {
                project.copy {
                    from(dep)
                    into(testDataBuildDir)
                }
            }
        }
    }

    tasks.withType<Jar> {
        destinationDirectory = testDataBuildDir
        dependsOn(copyIo)
    }
}

val build by tasks.creating {
    subprojects.forEach {
        dependsOn(it.tasks["build"])
    }
}