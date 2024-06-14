import org.gradle.api.tasks.testing.logging.TestExceptionFormat
import org.gradle.api.tasks.testing.logging.TestLogEvent

plugins {
    id("org.jetbrains.intellij.platform") version "2.0.0-beta6"
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
    apply(plugin = "org.jetbrains.intellij.platform")

    repositories {
        mavenCentral()
        intellijPlatform {
            defaultRepositories()
        }
    }
}

subprojects {
    apply(plugin = "java")

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

    dependencies {
        intellijPlatform {
            intellijIdeaCommunity(project.property("ideaVersion") as String)
        }
    }
}
