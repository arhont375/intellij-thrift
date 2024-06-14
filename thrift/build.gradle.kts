import org.jetbrains.intellij.platform.gradle.TestFrameworkType
import kotlin.math.sin

plugins {
    id("org.jetbrains.intellij.platform")
    id("java")
}

tasks {
    jar {
        archiveFileName = "thrift-jps.jar"
    }
}

sourceSets {
    main {
        java.srcDirs("src/main/gen")
    }
}

val junitVersion: String by project

repositories {
    mavenCentral()
    intellijPlatform {
        defaultRepositories()
    }
}

dependencies {
    implementation(project(":jps-plugin"))
    implementation("org.apache.commons:commons-lang3:3.14.0")

    testImplementation("org.junit.jupiter:junit-jupiter-api:$junitVersion")
    testImplementation("org.junit.platform:junit-platform-launcher:1.10.0")
    testRuntimeOnly("org.junit.vintage:junit-vintage-engine:$junitVersion")

    intellijPlatform {
        intellijIdeaCommunity(project.property("ideaVersion") as String)
        bundledPlugins("com.intellij.java")
        instrumentationTools()

        testFramework(TestFrameworkType.Plugin.Java)
    }
}

intellijPlatform {
    publishing {
        token = System.getenv()["JETBRAINS_TOKEN"] ?: ""
    }

    pluginConfiguration {
        version = System.getenv()["GITHUB_REF_NAME"] ?: "1.1.1"

        ideaVersion {
            sinceBuild = project.property("ideaSinceVersion") as String
        }
    }

    buildSearchableOptions = false
}
