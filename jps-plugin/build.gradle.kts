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
        create(IntelliJPlatformType.IntellijIdeaCommunity, project.property("ideaVersion") as String)
        bundledPlugins("com.intellij.java")
    }
}

intellijPlatform {
    buildSearchableOptions = false
}
