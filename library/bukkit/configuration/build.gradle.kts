crystalModule {
    name.set("Crystal Configuration")
    moduleName.set("configuration")
    description.set("Configurate-based configuration loader with custom serializers")
    library.set("bukkit")
}

dependencies {
    compileOnly(libs.bukkit)
    implementation(projects.bukkit.utils)
    api(libs.configurate)
}
