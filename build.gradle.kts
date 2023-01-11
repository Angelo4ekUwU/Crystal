plugins {
    `java-library`
    `maven-publish`
    id("org.ajoberstar.grgit") version "5.0.0"
}

allprojects {
    group = "io.sapphiremc"
    version = "1.2.3" + versionMetadata()
}

publishing {
    publications.create<MavenPublication>("maven") {
        pom.withXml {
            val depsNode = asNode().appendNode("dependencies")

            rootProject.subprojects.stream().filter {
                it.path.split(":").size == 2
            }.forEach {
                val depNode = depsNode.appendNode("dependency")
                depNode.appendNode("groupId", it.group)
                depNode.appendNode("artifactId", it.name)
                depNode.appendNode("version", it.version)
                depNode.appendNode("scope", "compile")
            }
        }
    }
}

fun versionMetadata(): String {
    val ghNumber = System.getenv("GITHUB_RUN_NUMBER")

    // CI builds only
    if (ghNumber != null) {
        return "+build.$ghNumber"
    }

    if (grgit != null) {
        val head = grgit.head()
        var id = head.abbreviatedId

        // Flag the build if the build tree is not clean
        if (!grgit.status().isClean) {
            id += "-dirty"
        }

        return "+rev.$id"
    }

    return "+unknown"
}
