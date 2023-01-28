crystalModule {
    name.set("Crystal Locale")
    moduleName.set("locale")
    description.set("Multiplatform locale module for Bukkit, BungeeCord and Velocity.")
    library.set("shared")
}

dependencies {
    compileOnlyApi(libs.slf4j)
    api(projects.shared.config)
}
