import org.jetbrains.intellij.platform.gradle.TestFrameworkType

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
        id = "thrift-syntax-fork"

        ideaVersion {
            sinceBuild = project.property("ideaSinceVersion") as String
            // Unset untilBuild version to declare compatibility outside of current major version
            untilBuild = provider { null }
        }
    }

    buildSearchableOptions = false
}
