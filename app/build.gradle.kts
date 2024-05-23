import kotlin.collections.*
plugins {
    id("com.android.application")
    id("kotlin-android")
    id("kotlin-kapt")
    id("kotlin-parcelize")
    id("dagger.hilt.android.plugin")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = BuildConfig.applicationId
    compileSdk = BuildConfig.compileSdk


    defaultConfig{
        applicationId = BuildConfig.applicationId
        minSdk=BuildConfig.minSdkVersion
        targetSdk = BuildConfig.targetSdkVersion
        versionCode = BuildConfig.versionCode
        versionName = BuildConfig.versionName
        testInstrumentationRunner = BuildConfig.testInstrumentationRunner

        ndk {
            //不配置则默认构建并打包所有可用的ABI
            //same with gradle-> abiFilters 'x86_64','armeabi-v7a','arm64-v8a'
            abiFilters.addAll(arrayListOf("x86_64", "armeabi-v7a", "arm64-v8a"))
        }
        // 开启 Dex 分包
        multiDexEnabled = true
    }

    signingConfigs {

        create("release") {
            keyAlias = SigningConfigs.key_alias
            keyPassword = SigningConfigs.key_password
            storeFile = file(SigningConfigs.store_file)
            storePassword = SigningConfigs.store_password
            enableV1Signing = true
            enableV2Signing = true
            enableV3Signing = true
            enableV4Signing = true
        }
    }

    buildTypes {
        debug {
            isMinifyEnabled = false
            isZipAlignEnabled = false
            isShrinkResources = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            signingConfig = signingConfigs.findByName("debug")
        }

        release {
            isMinifyEnabled = true
            isZipAlignEnabled = true
            isShrinkResources = true
            isDebuggable = false //是否debug
            isJniDebuggable  = false // 是否打开jniDebuggable开关
            isZipAlignEnabled = true //压缩优化

            proguardFiles (
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            signingConfig = signingConfigs.findByName("release")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }

    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.4.3"
    }

    packaging {
        resources {
            excludes.add("/META-INF/{AL2.0,LGPL2.1}")
        }
    }

}

dependencies {

    implementation (AndroidX.coreKtx)
    implementation (AndroidX.coresplashscreen)
    implementation (AndroidX.Lifecycle.runtimeKtx)
    implementation (AndroidX.Compose.activity)
    implementation (AndroidX.Compose.ui)
    implementation (AndroidX.Compose.tooling_preview)
    implementation (AndroidX.Compose.material3)
    implementation (AndroidX.Compose.runtime)
    implementation (AndroidX.Compose.ui_util)
    implementation (AndroidX.Compose.Accompanist.insets)
    implementation (AndroidX.Compose.Accompanist.placeholder)
    implementation (AndroidX.Compose.Accompanist.systemuicontroller)
    implementation (AndroidX.Paging.compose)
    implementation (AndroidX.Paging.runtimeKtx)
    implementation (AndroidX.Navigation.compose)
    implementation (AndroidX.Navigation.uiKtx)
    implementation (AndroidX.Navigation.animation)
    implementation (AndroidX.Work.runtime)
    implementation (AndroidX.Work.runtime_ktx)
    implementation (AndroidX.multidex)
    implementation (AndroidX.Documentfile)
    //Hilt
    implementation (AndroidX.Hilt.common)
    kapt (AndroidX.Hilt.compiler)
    implementation (AndroidX.Hilt.navigation_compose)

    //Room
    implementation(AndroidX.Room.runtime)
    kapt(AndroidX.Room.compiler)
    implementation(AndroidX.Room.ktx)

   // debugImplementation(ThirdPart.leakcanary)

    implementation(project(Lib.base))
    implementation(project(Lib.common))

    debugImplementation (AndroidX.Compose.uiTooling)
    debugImplementation (AndroidX.Compose.ui_test_manifest)

    testImplementation (Testing.junit)
    androidTestImplementation (Testing.androidJunit)
    androidTestImplementation (Testing.espresso)
    androidTestImplementation (Testing.compose_ui_test)
}