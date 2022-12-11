crystalModule {
    name.set("Crystal Database Connector")
    moduleName.set("database")
    description.set("Provides MySQL and SQLite connector")
    library.set("core")
}

dependencies {
    api(projects.core.base)
    api(libs.bundles.sql)
}
