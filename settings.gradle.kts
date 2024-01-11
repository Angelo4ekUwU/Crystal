@file:Suppress("UnstableApiUsage")

import java.nio.file.Files

dependencyResolutionManagement {
    repositories {
        mavenCentral()
        maven("https://papermc.io/repo/repository/maven-public/")
    }
}

rootProject.name = "crystal"

library("paper")
library("shared")

fun library(library: String) {
    include(library)

    val libraryProject = project(":$library")
    libraryProject.projectDir = file("library/$library")

    rootProject.projectDir.toPath().resolve("library/$library/").toFile().listFiles()?.forEach {
        // Is the module disabled?
        if (it.isDirectory
            && it.name != "src" // Ignore sources
            && it.name != "build" // Ignore build artifacts
            && !it.name.startsWith(".") // Ignore anything hidden on unix-like OSes
        ) {
            // Libraries can be disabled by adding a file named DISABLE at the root of its directory
            if (Files.exists(it.toPath().resolve("build.gradle.kts")) && Files.notExists(it.toPath().resolve("DISABLE"))) {
                include("$library:${it.name}")
            }
        }
    }
}
