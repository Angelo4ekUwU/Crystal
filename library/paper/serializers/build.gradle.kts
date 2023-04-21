plugins {
    id("io.papermc.paperweight.userdev") version "1.5.4"
}

crystalModule {
    name.set("Crystal Paper Serializers")
    moduleName.set("serializers")
    description.set("Configurate serializers for some paper objects")
    library.set("paper")
}

dependencies {
    paperweight.paperDevBundle(libs.paper.get().version)
    implementation(project(":paper:nms"))
    compileOnly(libs.configurate)
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
