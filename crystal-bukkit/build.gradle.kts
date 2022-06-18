plugins {
    id("com.github.johnrengelman.shadow")
}

repositories {
    maven("https://repo.extendedclip.com/content/repositories/placeholderapi/")
    maven("https://jitpack.io")
    maven("https://repo.codemc.io/repository/maven-public/")
    maven("https://maven.enginehub.org/repo/")
}

dependencies {
    compileOnly("io.sapphiremc.sapphire:sapphire-api:1.18.2-R0.1-SNAPSHOT")
    compileOnly("net.luckperms:api:5.4")
    compileOnly("com.github.MilkBowl:VaultAPI:1.7")
    compileOnly("io.sapphiremc.gold:gold-api:1.3.1-SNAPSHOT")
    compileOnly("me.clip:placeholderapi:2.11.1")
    compileOnly("me.filoghost.holographicdisplays:holographicdisplays-legacy-api-v2:3.0.0-SNAPSHOT")
    compileOnly("com.sk89q.worldedit:worldedit-bukkit:7.2.10")
    compileOnly(files(rootProject.projectDir.resolve("libs/CMI9.0.0.0API.jar")))
    compileOnly(files(rootProject.projectDir.resolve("libs/CMILib1.1.2.5.jar")))

    implementation("mysql:mysql-connector-java:8.0.29")
    implementation("com.zaxxer:HikariCP:5.0.1")
}

java {
    withSourcesJar()
    withJavadocJar()
}

publishing {
    publications.create<MavenPublication>("maven") {
        from(components["java"])
    }
}

tasks {
    build {
        dependsOn(shadowJar)
    }

    shadowJar {
        from(rootProject.projectDir.resolve("LICENSE").absolutePath)
        archiveClassifier.set("")
        relocate("com.zaxxer", "io.sapphiremc.crystal.lib")
        minimize()
    }
}
