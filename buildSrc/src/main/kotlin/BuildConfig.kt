/**
 * @author jiulang
 * @description 编译配置信息
 */
object BuildConfig {
    const val compileSdk = 33
    const val buildToolsVersion ="33"
    const val minSdkVersion = 21
    const val targetSdkVersion = 33

    const val applicationId ="com.wordsfairy.note"
    const val testInstrumentationRunner="androidx.test.runner.AndroidJUnitRunner"
    var versionName = "1.0.0"
    var versionCode = 1
}
object SigningConfigs{
    /** 密钥别名 */
    const val key_alias = "jl"

    /** 别名密码 */
    const val key_password = "jiulang"

    /** 密钥文件路径 */
    const val store_file = "../cert/note.jks"

    /** 密钥密码 */
    const val store_password = "jiulang"
}