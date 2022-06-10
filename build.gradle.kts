plugins {
    java
    `maven-publish`
    id("org.cadixdev.licenser") version "0.6.1"
    id("com.github.johnrengelman.shadow") version "7.1.2" apply false
    id("xyz.jpenilla.run-paper") version "1.0.6" apply false
}

allprojects {
    apply(plugin = "java")
    group = "io.sapphiremc.crystal"
    version = "0.1.0-SNAPSHOT"

    tasks {
        withType<JavaCompile> {
            options.encoding = Charsets.UTF_8.name()
            options.release.set(17)
        }
        withType<Javadoc> {
            options.encoding = Charsets.UTF_8.name()
        }
        withType<ProcessResources> {
            filteringCharset = Charsets.UTF_8.name()
        }
    }

    java {
        toolchain {
            languageVersion.set(JavaLanguageVersion.of(17))
        }
    }
}

subprojects {
    apply(plugin = "maven-publish")
    apply(plugin = "org.cadixdev.licenser")

    repositories {
        mavenCentral()
        maven("https://repo.extendedclip.com/content/repositories/placeholderapi/")
        maven("https://jitpack.io")
        maven("https://oss.sonatype.org/content/repositories/snapshots/")
        maven("https://repo.codemc.io/repository/maven-public/")
        maven("https://maven.enginehub.org/repo/")
        maven("http://repo.denaryworld.ru/snapshots/") {
            isAllowInsecureProtocol = true
        }
    }

    dependencies {
        compileOnly("io.sapphiremc.sapphire:sapphire-api:1.18.2-R0.1-SNAPSHOT")
        compileOnly("com.sk89q.worldedit:worldedit-bukkit:7.2.10")
        compileOnly("org.projectlombok:lombok:1.18.22")

        annotationProcessor("org.projectlombok:lombok:1.18.22")
    }

    license {
        include("**/io/sapphiremc/hideplayers/**")

        header(rootProject.file("HEADER"))
        newLine(false)
    }

    publishing {
        repositories {
            maven {
                name = "SapphireMC"
                url = uri("http://repo.denaryworld.ru/snapshots")
                isAllowInsecureProtocol = true
                credentials(PasswordCredentials::class)
            }
        }
    }

    tasks {
        compileJava {
            options.encoding = Charsets.UTF_8.name()
            options.compilerArgs.addAll(
                listOf(
                    "-parameters",
                    "-nowarn",
                    "-Xlint:-unchecked",
                    "-Xlint:-deprecation",
                    "-Xlint:-processing"
                )
            )
            options.isFork = true
        }

        processResources {
            filesMatching(listOf("plugin.yml", "config.yml", "messages.yml")) {
                expand("version" to project.version)
            }
        }
    }
}
