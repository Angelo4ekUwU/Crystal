@file:Suppress("UnstableApiUsage")

import java.nio.file.Files

dependencyResolutionManagement {
    repositories {
        mavenCentral()
        maven("https://repo.extendedclip.com/content/repositories/placeholderapi/")
        maven("https://papermc.io/repo/repository/maven-public/")
        maven("https://repo.codemc.org/repository/maven-public/")
        maven("https://maven.enginehub.org/repo/")
        maven("https://jitpack.io")
        maven("https://libraries.minecraft.net/")
        maven("https://the-planet.fun/repo/snapshots")
    }

    versionCatalogs {
        create("libs") {
            library("bukkit", "io.sapphiremc.sapphire:sapphire-api:1.19.4-R0.1-SNAPSHOT")

            library("slf4j", "org.slf4j:slf4j-api:2.0.7")
            library("annotations", "org.jetbrains:annotations:24.0.1")
            library("lombok", "org.projectlombok:lombok:1.18.26")
            library("commons-lang3", "org.apache.commons:commons-lang3:3.12.0")
            library("authlib", "com.mojang:authlib:3.18.38")

            val configurateVersion = "4.1.2"
            library("configurate", "org.spongepowered:configurate-core:$configurateVersion")
            library("hocon", "org.spongepowered:configurate-hocon:$configurateVersion")
            library("yaml", "org.spongepowered:configurate-yaml:$configurateVersion")
            library("gson", "org.spongepowered:configurate-gson:$configurateVersion")

            library("hikari", "com.zaxxer:HikariCP:5.0.1")
            library("mysql-connector", "com.mysql:mysql-connector-j:8.0.32")
            library("sqlite-jdbc", "org.xerial:sqlite-jdbc:3.41.2.1")

            bundle("sql", listOf("hikari", "mysql-connector"))
            bundle("configurate", listOf("hocon", "yaml", "gson"))
        }
    }
}

rootProject.name = "crystal"

library("shared")
library("bukkit")

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
