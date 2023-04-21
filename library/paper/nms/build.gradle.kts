plugins {
    id("io.papermc.paperweight.userdev") version "1.5.4"
}

crystalModule {
    name.set("Crystal Paper NMS")
    moduleName.set("nms")
    description.set("Simple NMS api with some methods")
    library.set("paper")
}

dependencies {
    paperweight.paperDevBundle(libs.paper.get().version)
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
