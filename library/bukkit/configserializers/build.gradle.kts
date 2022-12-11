crystalModule {
    name.set("Crystal Serializers")
    moduleName.set("configserializers")
    description.set("Configurate serializers for bukkit objects")
    library.set("bukkit")
}

dependencies {
    compileOnly(libs.bukkit)
    compileOnly(projects.core.config)
    api(projects.bukkit.utils)
}
