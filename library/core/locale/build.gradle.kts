crystalModule {
    name.set("Crystal Locale")
    moduleName.set("locale")
    description.set("Multiplatform locale module for Bukkit, BungeeCord and Velocity.")
    library.set("core")
}

dependencies {
    compileOnlyApi(libs.slf4j)
    api(projects.core.config)
}
