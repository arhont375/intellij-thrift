import org.jetbrains.intellij.platform.gradle.IntelliJPlatformType

plugins {
    id("org.jetbrains.intellij.platform.base")
    id("java")
}

tasks {
    jar {
        archiveFileName = "thrift-jps.jar"
    }
}

repositories {
    mavenCentral()
    intellijPlatform {
        defaultRepositories()
    }
}

dependencies {
    intellijPlatform {
        val rawIdeaVersion = project.property("ideaVersion") as String
        val ideaBuild = rawIdeaVersion.substringAfter("-", rawIdeaVersion)

        create(IntelliJPlatformType.IntellijIdeaCommunity, ideaBuild)
        bundledPlugins("com.intellij.java")
    }
}

intellijPlatform {
    buildSearchableOptions = false
}
