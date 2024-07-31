pluginManagement {
    val ideaPlatformPluginVersion: String by settings

    plugins {
        id("org.jetbrains.intellij.platform") version ideaPlatformPluginVersion
        id("org.jetbrains.intellij.platform.base") version ideaPlatformPluginVersion
    }

    repositories {
        maven("https://oss.sonatype.org/content/repositories/snapshots/")
        gradlePluginPortal()
    }
}

rootProject.name = "intellij-thrift"

include(":jps-plugin")
include(":thrift")
