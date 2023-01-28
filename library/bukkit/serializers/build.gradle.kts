crystalModule {
    name.set("Crystal Serializers")
    moduleName.set("serializers")
    description.set("Configurate serializers for some bukkit objects")
    library.set("bukkit")
}

dependencies {
    compileOnly(libs.bukkit)
    compileOnly(projects.shared.config)
    api(projects.bukkit.utils)
}
