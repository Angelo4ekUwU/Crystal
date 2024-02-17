crystalModule {
    name.set("Configurate Serializers")
    moduleName.set("serializers")
    description.set("Configurate serializers for some bukkit objects like ItemStack, Location")
    library.set("paper")
}

dependencies {
    compileOnly(libs.paper)
    compileOnly(libs.configurate.core)

    testImplementation(platform(libs.junit.bom))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testImplementation(libs.mockbukkit)
    testImplementation(libs.configurate.core)
    testImplementation(project(":paper:utils"))
}
