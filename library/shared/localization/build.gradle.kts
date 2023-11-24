crystalModule {
    name.set("Localization")
    moduleName.set("localization")
    description.set("Simple localization module for minecraft plugins")
    library.set("shared")
}

dependencies {
    compileOnlyApi(libs.slf4j)
    api(project(":shared:config"))
}

tasks.shadowJar {
    dependsOn(":shared:config:shadowJar")
}
