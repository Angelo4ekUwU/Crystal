@file:Suppress("UnstableApiUsage")

import java.nio.file.Files

dependencyResolutionManagement {
    repositories {
        mavenCentral()
        maven("https://papermc.io/repo/repository/maven-public/")
        maven("https://libraries.minecraft.net/")
        maven("https://jitpack.io")
    }

    versionCatalogs {
        create("libs") {
            library("paper", "io.papermc.paper:paper-api:1.20.2-R0.1-SNAPSHOT")

            library("slf4j", "org.slf4j:slf4j-api:2.0.9")
            library("annotations", "org.jetbrains:annotations:24.1.0")

            val configurateVersion = "4.1.2"
            library("configurate", "org.spongepowered:configurate-core:$configurateVersion")
            library("hocon", "org.spongepowered:configurate-hocon:$configurateVersion")
            library("yaml", "org.spongepowered:configurate-yaml:$configurateVersion")
            library("gson", "org.spongepowered:configurate-gson:$configurateVersion")

            library("hikari", "com.zaxxer:HikariCP:5.1.0")
            library("mysql-connector", "com.mysql:mysql-connector-j:8.2.0")
            library("sqlite-jdbc", "org.xerial:sqlite-jdbc:3.44.0.0")

            library("junit-bom", "org.junit:junit-bom:5.10.1")
            library("mockbukkit", "com.github.seeseemelk:MockBukkit-v1.20:3.47.0")

            bundle("sql", listOf("hikari", "mysql-connector", "sqlite-jdbc"))
            bundle("configurate", listOf("hocon", "yaml", "gson"))
        }
    }
}

rootProject.name = "crystal"

library("shared")
library("paper")

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
            if (Files.exists(it.toPath().resolve("build.gradle.kts")) && Files.notExists(
                    it.toPath().resolve("DISABLE")
                )
            ) {
                include("$library:${it.name}")
            }
        }
    }
}
