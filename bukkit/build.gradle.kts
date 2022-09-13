plugins {
    id("crystal.standart")
}

dependencies {
    api(libs.configurate)

    compileOnly(libs.bukkit)
    compileOnly(libs.luckperms)
    compileOnly(libs.vault)
    compileOnly(libs.gold)
    compileOnly(libs.placeholderapi)
    compileOnly(libs.holographicdisplays)
    compileOnly(libs.worldedit)
    compileOnly(libs.lombok)
    compileOnly(files(rootProject.projectDir.resolve("libs/cmi-api.jar")))
    compileOnly(files(rootProject.projectDir.resolve("libs/cmi-lib.jar")))

    implementation(libs.bundles.mysql)

    annotationProcessor(libs.lombok)
}

tasks {
    shadowJar {
        from(rootProject.projectDir.resolve("LICENSE").absolutePath)
        archiveClassifier.set("")
        relocate("com.zaxxer", "io.sapphiremc.crystal.lib")
        minimize()
    }
}
