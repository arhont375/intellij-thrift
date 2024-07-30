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

    intellijPlatform {
        create(project.property("ideaVersion") as String)
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
        id = "thrift-syntax-fork"

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
