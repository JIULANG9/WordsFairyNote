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