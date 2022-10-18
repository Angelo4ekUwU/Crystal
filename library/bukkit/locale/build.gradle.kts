crystalModule {
    name.set("Crystal Locale")
    moduleName.set("locale")
    description.set("")
    library.set("bukkit")
}

dependencies {
    compileOnly(libs.bukkit)
    implementation(projects.bukkit.configuration)
    implementation(projects.bukkit.utils)
}
