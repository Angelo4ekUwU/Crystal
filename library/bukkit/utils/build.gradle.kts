crystalModule {
    name.set("Crystal Utils")
    moduleName.set("utils")
    description.set("")
    library.set("bukkit")
}

dependencies {
    compileOnly(libs.bukkit)
    compileOnly(libs.lombok)
    annotationProcessor(libs.lombok)
}
