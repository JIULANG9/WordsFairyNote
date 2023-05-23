// Top-level build file where you can add configuration options common to all sub-projects/modules.
buildscript {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
        maven("https://jitpack.io")


    }
    dependencies {
        classpath(Gradle.plugin)
        classpath(Kotlin.plugin)
        classpath(AndroidX.Hilt.gradlePlugin)
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.8.10")
    }
}

allprojects{
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
        maven("https://jitpack.io")


    }
}

tasks.register("clean", Delete::class) {
    delete(rootProject.buildDir)
}