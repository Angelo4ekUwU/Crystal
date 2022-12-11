@file:Suppress("UnstableApiUsage")

import java.nio.file.Files

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

dependencyResolutionManagement {
    repositories {
        mavenCentral()
        maven("https://repo.extendedclip.com/content/repositories/placeholderapi/")
        maven("https://papermc.io/repo/repository/maven-public/")
        maven("https://repo.codemc.org/repository/maven-public/")
        maven("https://maven.enginehub.org/repo/")
        maven("https://jitpack.io")
        maven("http://repo.denaryworld.ru/snapshots") {
            isAllowInsecureProtocol = true
        }
    }

    versionCatalogs {
        create("libs") {
            library("bukkit", "io.sapphiremc.sapphire:sapphire-api:1.19.2-R0.1-SNAPSHOT")
            library("placeholderapi", "me.clip:placeholderapi:2.11.2")
            library("luckperms", "net.luckperms:api:5.4")
            library("vault", "com.github.MilkBowl:VaultAPI:1.7")
            library("holographicdisplays", "me.filoghost.holographicdisplays:holographicdisplays-legacy-api-v2:3.0.0-SNAPSHOT")
            library("gold", "io.sapphiremc.gold:gold-api:1.3.1")
            library("worldedit", "com.sk89q.worldedit:worldedit-bukkit:7.2.12")
            library("nbt", "de.tr7zw:item-nbt-api:2.10.0")

            library("slf4j", "org.slf4j:slf4j-api:2.0.3")
            library("annotations", "org.jetbrains:annotations:23.0.0")
            library("lombok", "org.projectlombok:lombok:1.18.24")
            library("commons-lang3", "org.apache.commons:commons-lang3:3.12.0")

            val configurateVersion = "4.1.2"
            library("configurate", "org.spongepowered:configurate-core:$configurateVersion")
            library("hocon", "org.spongepowered:configurate-hocon:$configurateVersion")
            library("yaml", "org.spongepowered:configurate-yaml:$configurateVersion")
            library("gson", "org.spongepowered:configurate-gson:$configurateVersion")

            library("hikari", "com.zaxxer:HikariCP:5.0.1")
            library("mysql-connector", "mysql:mysql-connector-java:8.0.29")
            library("sqlite-jdbc", "org.xerial:sqlite-jdbc:3.39.3.0")

            bundle("sql", listOf("hikari", "mysql-connector"))
            bundle("configurate", listOf("hocon", "yaml", "gson"))
        }
    }
}

rootProject.name = "crystal"

include("plugin")

library("core")
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
