plugins {
    id("com.github.johnrengelman.shadow")
    id("xyz.jpenilla.run-paper")
}

dependencies {
    implementation(project(":crystal-core"))
}

tasks {
    build {
        dependsOn(shadowJar)
    }

    shadowJar {
        archiveClassifier.set("")
        minimize()
    }

    runServer {
        minecraftVersion("1.18.2")
        runDirectory.set(rootProject.projectDir.resolve("run/"))
        if (!System.getenv("useCustomCore").isNullOrEmpty()) {
            serverJar.set(rootProject.projectDir.resolve("run/server.jar"))
        }
    }
}