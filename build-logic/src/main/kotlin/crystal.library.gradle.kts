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

publishing {
    publications.create<MavenPublication>("shadow") {
        project.shadow.component(this)
    }
}
