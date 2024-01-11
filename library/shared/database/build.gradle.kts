crystalModule {
    name.set("Database Connector")
    moduleName.set("database")
    description.set("Library for SQL-based databases")
    library.set("shared")
}

dependencies {
    compileOnlyApi(libs.annotations)
    api(libs.bundles.sql)
}
