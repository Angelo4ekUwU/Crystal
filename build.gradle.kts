plugins {
    `java-library`
    `maven-publish`
}

allprojects {
    group = "me.denarydev"
    version = "2.0.0" + getVersionInfo()
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

fun getVersionInfo(): String {
    if (!System.getenv("CI").isNullOrEmpty()) {
        return "-SNAPSHOT"
    }

    if (!System.getenv("RELEASE").isNullOrEmpty()) {
        return ""
    }

    return "-local"
}
