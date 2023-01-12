crystalModule {
    name.set("Crystal Config")
    moduleName.set("config")
    description.set("Configurate-based configuration loaders")
    library.set("core")
}

dependencies {
    compileOnlyApi(libs.annotations)
    api(libs.bundles.configurate)
}
