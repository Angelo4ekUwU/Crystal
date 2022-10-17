crystalModule {
    name.set("Crystal Configuration")
    moduleName.set("configuration")
    description.set("Configurate-based configuration loader with custom serializers")
    library.set("bukkit")
}

dependencies {
    compileOnly(libs.bukkit)
    implementation(projects.bukkit.utils)
    api(libs.bundles.configurate) {
        exclude("com.google.code.gson")
        exclude("com.google.errorprone")
        exclude("org.yaml")
        exclude("org.checkerframework")
    }
}
