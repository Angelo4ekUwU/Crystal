plugins {
    id("crystal.base")
    `maven-publish`
    id("com.github.johnrengelman.shadow")
}

java {
    withSourcesJar()
    withJavadocJar()
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

    publications.create<MavenPublication>("maven") {
        groupId = rootProject.group as String
        artifactId = "crystal-${project.name}"
        version = rootProject.version as String
        from(components["java"])
    }
}

tasks {
    build {
        dependsOn(shadowJar)
    }
}
