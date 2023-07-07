plugins {
    id("com.android.library")
    kotlin("android")
    kotlin("kapt")
}

android {
    // 编译 SDK 版本
    compileSdk = BuildConfig.compileSdk
    namespace = "com.wordsfairy.base"

    // 资源前缀
    resourcePrefix("base")

    defaultConfig {
        // 最低支持版本
        minSdk=BuildConfig.minSdkVersion
        targetSdk = BuildConfig.targetSdkVersion

    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
}

dependencies {

    // Kotlin 支持
    implementation(Kotlin.stdlib)
    implementation(AndroidX.coreKtx)
    implementation(AndroidX.DataStore.preferences)
    implementation(AndroidX.DataStore.core)

}