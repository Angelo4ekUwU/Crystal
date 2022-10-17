crystalModule {
    name.set("Crystal Database Connector")
    moduleName.set("database")
    description.set("Provides MySQL and SQLite connector")
    library.set("core")
}

dependencies {
    api(libs.bundles.sql)
    implementation(libs.annotations)
}
