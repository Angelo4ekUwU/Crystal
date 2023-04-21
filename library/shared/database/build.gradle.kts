crystalModule {
    name.set("Crystal Shared DB Connector")
    moduleName.set("database")
    description.set("Provides api for MySQL and SQLite")
    library.set("shared")
}

dependencies {
    compileOnlyApi(libs.annotations)
    api(libs.bundles.sql)
}
