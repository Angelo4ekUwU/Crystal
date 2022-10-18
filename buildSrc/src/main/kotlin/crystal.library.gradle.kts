import crystal.extension.CrystalLibraryExtension

plugins {
    id("crystal.base")
}

val extension = project.extensions.create("crystalLibrary", CrystalLibraryExtension::class, project)

// We seem to have to use afterEvaluate to verify this stuff
afterEvaluate {
    // TODO: Validate the extension parts
}

java {
    withSourcesJar()
    withJavadocJar()
}

afterEvaluate {
    group = "io.sapphiremc.crystal"

    subprojects.forEach {
        it.plugins.apply("crystal.module")
    }

    publishing {
        publications.create<MavenPublication>("maven") {
            pom.withXml {
                val depsNode = asNode().appendNode("dependencies")

                project.subprojects.stream().filter {
                    it.path.split(":").size == 3
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

    // Print the version and throw an exception if it doesn't match
    if (version != rootProject.version) {
        throw GradleException("Library ${extension.libraryName.get()} version ($version) does not match root project version ($rootProject.version). Do not change it!")
    }
}

tasks {
    // A library isn't truly a distributed artifact, rather it is just maven metadata to depend on the modules of a library.
    // For that reason, we have no use for the jar or remapJar tasks.
    jar {
        enabled = false
    }
}
