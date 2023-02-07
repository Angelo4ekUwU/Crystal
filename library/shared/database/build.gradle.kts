crystalModule {
    name.set("Crystal Database Connector")
    moduleName.set("database")
    description.set("Provides api for MySQL and SQLite")
    library.set("shared")
}

dependencies {
    compileOnlyApi(libs.annotations)
    api(libs.bundles.sql)
}