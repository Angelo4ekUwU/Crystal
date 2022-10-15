plugins {
    id("crystal.module")
}

crystalModule {
    name.set("Crystal Bukkit Compat")
    moduleName.set("compat")
    description.set("")
    library.set("bukkit")
}

dependencies {
    implementation(projects.bukkit.utils)
    implementation(libs.nbt)
    compileOnly(libs.bukkit)
}

tasks.shadowJar {
    relocate("de.tr7zw.changeme", "io.sapphiremc.crystal.lib")
}
