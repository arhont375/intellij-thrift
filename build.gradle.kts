import org.gradle.api.tasks.testing.logging.TestExceptionFormat
import org.gradle.api.tasks.testing.logging.TestLogEvent

plugins {
    id("java")
    id("idea")
}

subprojects {
    apply(plugin = "java")
    apply(plugin = "idea")

    idea {
        module {
            if (System.getenv("CI").toBoolean()) {
                isDownloadSources = true
                isDownloadJavadoc = true
            }
        }
    }

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
