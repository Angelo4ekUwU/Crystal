import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    `kotlin-dsl`
}

repositories {
    gradlePluginPortal()
}

dependencies {
    implementation("com.gradleup.shadow:shadow-gradle-plugin:8.3.5")
    implementation("com.diffplug.spotless:spotless-plugin-gradle:6.25.0")
    implementation("gradle.plugin.org.cadixdev.gradle:licenser:0.6.1")
    implementation("org.gradlex:extra-java-module-info:1.9")
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(17))
}

kotlin {
    jvmToolchain(17)
}

tasks {
    withType<JavaCompile>().configureEach {
        options.release.set(17)
    }

    withType<KotlinCompile>().configureEach {
        kotlinOptions {
            jvmTarget = "17"
        }
    }
}
