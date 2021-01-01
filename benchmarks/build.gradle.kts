buildscript {
    repositories {
        jcenter()
        maven("https://plugins.gradle.org/m2/")
    }
    dependencies {
        classpath("me.champeau.gradle:jmh-gradle-plugin:0.5.0")
    }
}


plugins {
    kotlin("jvm")
    kotlin("kapt")
    kotlin("plugin.allopen") version "1.3.61"
    id("me.champeau.gradle.jmh") version "0.5.0"
}

sourceSets {
    named("jmh") {
        compileClasspath += parent!!.sourceSets.main.get().runtimeClasspath
    }
}

allOpen {
    annotation("org.openjdk.jmh.annotations.State")
}

jmh {
    duplicateClassesStrategy = DuplicatesStrategy.WARN
}

repositories {
    mavenCentral()
    jcenter()
    maven("https://oss.sonatype.org/content/repositories/snapshots")
    maven("https://dl.bintray.com/kotlin/kotlinx/")
}

dependencies {
    jmh("org.openjdk.jmh:jmh-core:1.21")
    jmh("org.openjdk.jmh:jmh-generator-annprocess:1.21")
    jmh(parent!!)
}