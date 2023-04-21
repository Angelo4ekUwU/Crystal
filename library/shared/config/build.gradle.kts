crystalModule {
    name.set("Crystal Shared Config")
    moduleName.set("config")
    description.set("Configurate-based configuration loaders")
    library.set("shared")
}

dependencies {
    compileOnlyApi(libs.annotations)
    api(libs.bundles.configurate)
}
