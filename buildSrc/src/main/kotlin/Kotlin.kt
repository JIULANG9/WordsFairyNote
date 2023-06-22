/**
 * @author phz
 * @introduction 协程等kotlin内部使用类
 */
object Kotlin {
    var kotlin_version = "1.8.10"
    
    //Kotlin 1.4 以后，您不再需要在 gradle 上声明 stdlib
    var stdlib ="org.jetbrains.kotlin:kotlin-stdlib:$kotlin_version"
    var reflect ="org.jetbrains.kotlin:kotlin-reflect:$kotlin_version"
    val stdlibJdk7 = "org.jetbrains.kotlin:kotlin-stdlib-jdk7:$kotlin_version"
    val stdlibJdk8 = "org.jetbrains.kotlin:kotlin-stdlib-jdk8:$kotlin_version"
    val test = "org.jetbrains.kotlin:kotlin-test-junit:$kotlin_version"
    val plugin = "org.jetbrains.kotlin:kotlin-gradle-plugin:$kotlin_version"

    val serialization = "org.jetbrains.kotlinx:kotlinx-serialization-json:1.5.0"
    val serialization_protobuf = "org.danbrough.kotlinx:kotlinx-serialization-protobuf:1.5.0"

    //协程
    object Coroutines{
        private const val version = "1.5.1"
        const val core ="org.jetbrains.kotlinx:kotlinx-coroutines-core:$version"
        const val android ="org.jetbrains.kotlinx:kotlinx-coroutines-android:$version"
    }
}