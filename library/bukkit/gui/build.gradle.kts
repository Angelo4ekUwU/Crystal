crystalModule {
    name.set("Crystal Bukkit Compat")
    moduleName.set("compat")
    description.set("")
    library.set("bukkit")
}

dependencies {
    compileOnly(libs.bukkit)
    api(projects.bukkit.compatibility)
}
