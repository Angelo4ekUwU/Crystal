plugins {
    id("crystal.module")
}

crystalModule {
    name.set("Crystal Bukkit Base")
    moduleName.set("base")
    description.set("")
    library.set("bukkit")
}

dependencies {
    api(libs.configurate)
    api(projects.bukkit.locale)
    api(projects.bukkit.compat)
    api(projects.core.sql)

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

    annotationProcessor(libs.lombok)
}
