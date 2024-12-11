import crystal.extension.CrystalLibraryExtension

plugins {
    id("crystal.base")
}

val extension = project.extensions.create("crystalLibrary", CrystalLibraryExtension::class, project)

java {
    withSourcesJar()
    withJavadocJar()
}

afterEvaluate {
    group = "${rootProject.group}.crystal"

    subprojects.forEach {
        it.plugins.apply("crystal.module")
    }

    // Print the version and throw an exception if it doesn't match
    if (version != rootProject.version) {
        throw GradleException("Library ${extension.libraryName.get()} version ($version) does not match root project version ($rootProject.version). Do not change it!")
    }
}
