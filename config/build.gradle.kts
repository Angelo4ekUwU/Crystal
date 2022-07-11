plugins {
    id("crystal.standart")
}

dependencies {
    implementation(libs.commons.lang3)
    implementation(libs.annotations)

    api(libs.snakeyaml)
}
