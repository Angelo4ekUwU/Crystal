plugins {
    id("crystal.module")
}

crystalModule {
    name.set("Crystal SQL Connector")
    moduleName.set("sql")
    description.set("Provides MySQL and SQLite connector")
    library.set("core")
}

dependencies {
    api(libs.bundles.sql)

    implementation(libs.annotations)
}
