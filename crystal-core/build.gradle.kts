plugins {
    id("com.github.johnrengelman.shadow")
    id("org.cadixdev.licenser")
}

dependencies {
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