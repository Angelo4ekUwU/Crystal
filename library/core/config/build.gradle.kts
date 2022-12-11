crystalModule {
    name.set("Crystal Config")
    moduleName.set("config")
    description.set("Configurate-based configuration loaders")
    library.set("core")
}

dependencies {
    api(projects.core.base)
    api(libs.bundles.configurate)
}
