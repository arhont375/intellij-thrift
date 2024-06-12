import org.gradle.api.tasks.testing.logging.TestExceptionFormat
import org.gradle.api.tasks.testing.logging.TestLogEvent
import org.jetbrains.intellij.IntelliJPluginExtension
import org.jetbrains.intellij.tasks.BuildSearchableOptionsTask

plugins {
    id("org.jetbrains.intellij") version "1.17.3" apply false
    id("java")
    id("idea")
}

idea {
    module {
        isDownloadSources = true
        isDownloadJavadoc = true
    }
}

allprojects {
    repositories {
        mavenCentral()
    }
}

subprojects {
    apply(plugin = "java")
    apply(plugin = "org.jetbrains.intellij")

    java {
        toolchain {
            languageVersion = JavaLanguageVersion.of(17)
        }
    }

    tasks.withType<JavaCompile>().configureEach {
        options.release = JavaLanguageVersion.of(8).asInt()
    }

    tasks {
        test {
            useJUnitPlatform()

            testLogging {
                exceptionFormat = TestExceptionFormat.FULL
                events = setOf(TestLogEvent.PASSED, TestLogEvent.SKIPPED, TestLogEvent.FAILED)
            }
        }
    }

    extensions.configure<IntelliJPluginExtension> {
        version.set(project.property("ideaVersion") as String)
        plugins.set(listOf("copyright", "java"))
        downloadSources.set(true)
    }

    tasks.withType<BuildSearchableOptionsTask> {
        enabled = false
    }
}
