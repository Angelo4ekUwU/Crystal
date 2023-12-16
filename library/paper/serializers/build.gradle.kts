plugins {
    alias(libs.plugins.paperweight)
}

crystalModule {
    name.set("Crystal Paper Serializers")
    moduleName.set("serializers")
    description.set("Configurate serializers for some paper objects")
    library.set("paper")
}

dependencies {
    paperweight.paperDevBundle(libs.versions.paper.get())
    implementation(project(":paper:nms"))
    compileOnly(libs.configurate.core)

    testImplementation(platform(libs.junit.bom))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testImplementation(libs.mockbukkit)
}

tasks {
    build {
        dependsOn(reobfJar)
    }

    withType<AbstractPublishToMaven> {
        dependsOn(reobfJar)
    }
}

afterEvaluate {
    publishing {
        publications.create<MavenPublication>("shadow") {
            artifact(tasks.reobfJar.get().outputJar.get())
            artifact(tasks["sourcesJar"])
            artifact(tasks["javadocJar"])
        }
    }
}
