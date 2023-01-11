plugins {
    `java-library`
    `maven-publish`
    id("com.github.johnrengelman.shadow")
}

tasks {
    compileJava {
        options.encoding = Charsets.UTF_8.name()
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
    toolchain.languageVersion.set(JavaLanguageVersion.of(8))
}

val env: Map<String, String> = System.getenv()

publishing {
    repositories {
        if (env.containsKey("MAVEN_URL")) {
            maven(env["MAVEN_URL"]!!) {
                name = "SapphireMC"
                if (env["MAVEN_URL"]!!.startsWith("http://"))
                    isAllowInsecureProtocol = true
                credentials {
                    username = env["MAVEN_USERNAME"]
                    password = env["MAVEN_PASSWORD"]
                }
            }
        }
    }
}
