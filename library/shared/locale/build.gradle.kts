crystalModule {
    name.set("Crystal Shared Locale")
    moduleName.set("locale")
    description.set("Simple locale module for plugins")
    library.set("shared")
}

dependencies {
    compileOnlyApi(libs.slf4j)
    api(project(":shared:config"))
}

tasks.shadowJar {
    dependsOn(":shared:config:shadowJar")
}
