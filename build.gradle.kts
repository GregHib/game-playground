plugins {
    kotlin("jvm") version "1.3.61"
    id("org.openjfx.javafxplugin") version "0.0.8"
}

group = "world.gregs.game.playground"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    jcenter()
    maven("https://oss.sonatype.org/content/repositories/snapshots")
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-javafx:1.3.3")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.3.3")
    implementation("no.tornado:tornadofx:2.0.0-SNAPSHOT")

    testImplementation("org.junit.jupiter:junit-jupiter-api:5.5.2")
    testImplementation("io.mockk:mockk:1.9.3")
    testImplementation("org.amshove.kluent:kluent:1.59")
}

val javafxModules = arrayOf("controls", "fxml", "graphics")

javafx {
    modules = javafxModules.map { "javafx.$it" }
}


tasks {
    compileKotlin {
        kotlinOptions.jvmTarget = "1.8"
    }
    compileTestKotlin {
        kotlinOptions.jvmTarget = "1.8"
    }
}