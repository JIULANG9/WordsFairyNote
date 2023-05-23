plugins {
    id("com.android.library")
    kotlin("android")
    kotlin("kapt")
}



android {
    // 编译 SDK 版本
    compileSdk = BuildConfig.compileSdk

    // 资源前缀
    resourcePrefix("base")

    defaultConfig {
        // 最低支持版本
        minSdk=BuildConfig.minSdkVersion
        targetSdk = BuildConfig.targetSdkVersion

    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
}

dependencies {

    // Kotlin 支持
    implementation(Kotlin.stdlib)
    implementation(Kotlin.reflect)
    implementation(AndroidX.coreKtx)
    implementation(AndroidX.DataStore.preferences)
    implementation(AndroidX.DataStore.core)

}