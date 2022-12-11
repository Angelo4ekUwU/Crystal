crystalModule {
    name.set("Crystal Base")
    moduleName.set("base")
    description.set("Base module for all crystal modules")
    library.set("core")
}

dependencies {
    api(libs.annotations)
    compileOnlyApi(libs.slf4j)
}
