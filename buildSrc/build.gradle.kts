import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    `kotlin-dsl`
}

repositories {
    gradlePluginPortal()
}

dependencies {
    implementation("com.github.johnrengelman:shadow:8.1.1")
    implementation("com.diffplug.spotless:spotless-plugin-gradle:6.25.0")
    implementation("gradle.plugin.org.cadixdev.gradle:licenser:0.6.1")
}

tasks.withType<KotlinCompile>().configureEach {
    kotlinOptions {
        jvmTarget = "17"
    }
}
