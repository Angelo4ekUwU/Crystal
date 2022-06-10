plugins {
    id("com.github.johnrengelman.shadow")
    id("org.cadixdev.licenser")
}

dependencies {
    compileOnly("net.luckperms:api:5.4")
    compileOnly("com.github.MilkBowl:VaultAPI:1.7")
    compileOnly("io.sapphiremc.gold:gold-api:1.3.1-SNAPSHOT")
    compileOnly("me.clip:placeholderapi:2.11.1")
    compileOnly("me.filoghost.holographicdisplays:holographicdisplays-legacy-api-v2:3.0.0-SNAPSHOT")
    compileOnly(files(rootProject.projectDir.resolve("libs/CMI9.0.0.0API.jar")))
    compileOnly(files(rootProject.projectDir.resolve("libs/CMILib1.1.2.5.jar")))

    implementation("mysql:mysql-connector-java:8.0.29")
    implementation("com.zaxxer:HikariCP:5.0.1")
}

license {
    include("**/io/sapphiremc/hideplayers/**")

    header(rootProject.file("HEADER"))
    newLine(false)
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
