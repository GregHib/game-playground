plugins {
    kotlin("jvm") version "1.3.61"
    id("org.openjfx.javafxplugin") version "0.0.8"
    kotlin("kapt") version "1.3.61"
}

group = "world.gregs.game.playground"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    jcenter()
    maven("https://oss.sonatype.org/content/repositories/snapshots")
    maven("https://dl.bintray.com/kotlin/kotlinx/")
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-javafx:1.3.3")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.3.3")
    implementation("no.tornado:tornadofx:2.0.0-SNAPSHOT")
    implementation("ch.ethz.globis.phtree:phtree:2.4.0")
    implementation("org.jgrapht:jgrapht-core:1.3.1")
    implementation("org.jgrapht:jgrapht-ext:1.3.1")
    implementation("it.unimi.dsi:fastutil:8.3.0")
    implementation("es.usc.citius.hipster:hipster-all:1.0.1")

    testImplementation("org.junit.jupiter:junit-jupiter-api:5.5.2")
    testImplementation("io.mockk:mockk:1.9.3")
    testImplementation("org.amshove.kluent:kluent:1.59")
    testImplementation("pl.pragmatists:JUnitParams:1.1.1")
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