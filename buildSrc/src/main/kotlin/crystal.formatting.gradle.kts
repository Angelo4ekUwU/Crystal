import com.diffplug.spotless.LineEnding

plugins {
    id("org.cadixdev.licenser")
    id("com.diffplug.spotless")
}

license.include("**/me/denarydev/crystal/**")

license.header(rootProject.file("HEADER"))
license.newLine(false)

spotless {
    java {
        target("**/me/denarydev/crystal/**")

        trimTrailingWhitespace()
        indentWithSpaces(4)
        endWithNewline()
        lineEndings = LineEnding.UNIX
    }
}
