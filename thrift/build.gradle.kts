import org.jetbrains.intellij.platform.gradle.IntelliJPlatformType
import org.jetbrains.intellij.platform.gradle.TestFrameworkType
import org.jetbrains.intellij.platform.gradle.models.ProductRelease

plugins {
    id("org.jetbrains.intellij.platform")
    id("java")
}

tasks {
    jar {
        archiveFileName = "plugin.jar"
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

    // Only for the ThriftParserTest
    testImplementation("junit:junit:4.13.1")
    testImplementation(platform("org.junit:junit-bom:$junitVersion"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    testRuntimeOnly("org.junit.vintage:junit-vintage-engine")

    implementation("org.awaitility:awaitility:4.2.1")

    intellijPlatform {
        create(project.property("ideaVersion") as String)
        bundledPlugins("com.intellij.java")
        instrumentationTools()

        testFramework(TestFrameworkType.Plugin.Java)
    }
}


intellijPlatform {
    val gitTag = System.getenv()["GITHUB_REF_NAME"] ?: ""

    publishing {
        token = System.getenv()["JETBRAINS_TOKEN"] ?: ""
        if (gitTag.endsWith("-eap")) {
            channels.set(listOf("eap"))
        }
    }

    pluginConfiguration {
        version = gitTag
        id = "thrift-syntax-fork"
        changeNotes = """
            2024.2 EAP support
        """.trimIndent()

        ideaVersion {
            sinceBuild = project.property("ideaSinceVersion") as String
            // Unset untilBuild version to declare compatibility outside of current major version
            untilBuild = provider { null }
        }
    }

    buildSearchableOptions = false
}

tasks {
    printProductsReleases {
        channels = listOf(ProductRelease.Channel.EAP)
        types = listOf(IntelliJPlatformType.IntellijIdeaCommunity)
        untilBuild = provider { null }
    }
}
