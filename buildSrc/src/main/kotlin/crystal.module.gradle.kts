import crystal.extension.CrystalModuleExtension

plugins {
    id("crystal.base")
    `maven-publish`
}

val extension = project.extensions.create("crystalModule", CrystalModuleExtension::class, project)

afterEvaluate {
    group = "me.denarydev.crystal.${extension.library.get()}"

    java {
        withSourcesJar()
        withJavadocJar()
    }

    if (!setOf(":paper:nms", ":paper:serializers").contains(project.path)) {
        publishing {
            publications.create<MavenPublication>("shadow") {
                artifact(tasks["shadowJar"])
                artifact(tasks["sourcesJar"])
                artifact(tasks["javadocJar"])
            }
        }
    }
}
