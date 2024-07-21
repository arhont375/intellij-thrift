import org.gradle.api.tasks.testing.logging.TestExceptionFormat
import org.gradle.api.tasks.testing.logging.TestLogEvent

plugins {
    id("org.jetbrains.intellij.platform") version "2.0.0-rc1" apply false
    id("java")
    id("idea")
}

idea {
    module {
        isDownloadSources = true
        isDownloadJavadoc = true
    }
}

subprojects {
    apply(plugin = "java")

    java {
        toolchain {
            languageVersion = JavaLanguageVersion.of(21)
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
}
