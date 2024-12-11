plugins {
    `java-library`
    `maven-publish`
    id("com.github.johnrengelman.shadow")
    id("crystal.formatting")
}

tasks {
    withType<AbstractPublishToMaven> {
        dependsOn(jar)
    }

    compileJava {
        options.encoding = Charsets.UTF_8.name()
        options.release.set(17)
        options.compilerArgs.addAll(
            listOf(
                "-parameters",
                "-nowarn",
                "-Xlint:-unchecked",
                "-Xlint:-deprecation",
                "-Xlint:-processing"
            )
        )
        options.isFork = true
    }

    javadoc {
        options.encoding = Charsets.UTF_8.name()
        (options as StandardJavadocDocletOptions).addStringOption("Xdoclint:none", "-quiet")
    }

    build {
        dependsOn(shadowJar)
    }

    shadowJar {
        archiveClassifier.set("")
        duplicatesStrategy = DuplicatesStrategy.INCLUDE
    }
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(17))
}

val env: Map<String, String> = System.getenv()
val repo = if (rootProject.version.toString().endsWith("-SNAPSHOT")) "snapshots" else "releases"

publishing {
    repositories {
        maven("https://repo.activmine.ru/$repo/") {
            name = "activmine"
            credentials(PasswordCredentials::class)
        }
    }
}
