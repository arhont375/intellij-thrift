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

dependencies {
    implementation(project(":jps-plugin"))
    implementation("org.apache.commons:commons-lang3:3.14.0")

    testImplementation("org.junit.jupiter:junit-jupiter-api:$junitVersion")
    testImplementation("org.junit.platform:junit-platform-launcher:1.10.0")
    testRuntimeOnly("org.junit.vintage:junit-vintage-engine:$junitVersion")
}

tasks {
    publishPlugin {
        token = System.getenv()["JETBRAINS_TOKEN"] ?: ""
    }

    patchPluginXml {
        version = System.getenv()["GITHUB_REF_NAME"] ?: "1.1.1"
    }
}
