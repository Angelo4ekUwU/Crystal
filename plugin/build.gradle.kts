plugins {
    java
    id("com.github.johnrengelman.shadow")
}

repositories {
    maven("https://repo.papermc.io/repository/maven-public/")
    maven("http://repo.denaryworld.ru/snapshots/") { isAllowInsecureProtocol = true }
}

dependencies {
    compileOnly("io.sapphiremc.sapphire:sapphire-api:1.19.2-R0.1-SNAPSHOT")

    listOf(
        projects.bukkit.compatibility,
        projects.bukkit.configuration,
        projects.bukkit.gui,
        projects.bukkit.locale,
        projects.bukkit.utils,
        projects.core.database
    ).forEach {
        implementation(it)
    }
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(17))
}

tasks {
    withType<JavaCompile> {
        options.encoding = Charsets.UTF_8.name()
        options.release.set(17)
    }

    withType<ProcessResources> {
        filteringCharset = Charsets.UTF_8.name()
    }

    processResources {
        inputs.properties("version" to project.version)

        filesMatching("plugin.yml") {
            expand("version" to project.version)
        }
    }

    build {
        dependsOn(shadowJar)
    }

    jar {
        archiveBaseName.set("Crystal")
    }

    shadowJar {
        archiveBaseName.set("Crystal")
        archiveClassifier.set("")
    }
}
