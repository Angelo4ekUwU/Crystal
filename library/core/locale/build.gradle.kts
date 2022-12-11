crystalModule {
    name.set("Crystal Locale")
    moduleName.set("locale")
    description.set("")
    library.set("bukkit")
}

dependencies {
    api(projects.core.base)
    implementation(projects.core.config)
    implementation(projects.bukkit.utils)
}
