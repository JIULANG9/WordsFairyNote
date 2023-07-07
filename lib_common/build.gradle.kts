plugins {
    id("java-library")
    kotlin("jvm")

}

dependencies {
    // kotlin
    implementation(Kotlin.stdlib)
    implementation(Kotlin.reflect)
}
java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}