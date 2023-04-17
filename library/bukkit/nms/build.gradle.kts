plugins {
    id("io.papermc.paperweight.userdev") version "1.5.4"
}

crystalModule {
    name.set("Crystal Bukkit NMS")
    moduleName.set("nms")
    description.set("")
    library.set("bukkit")
}

repositories {
    maven("https://the-planet.fun/repo/snapshots")
}

dependencies {
    paperweight.devBundle("io.sapphiremc.sapphire", libs.bukkit.get().version)
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
        publications.create<MavenPublication>("reobf") {
            artifact(tasks.reobfJar.get().outputJar.get())
        }
    }
}
