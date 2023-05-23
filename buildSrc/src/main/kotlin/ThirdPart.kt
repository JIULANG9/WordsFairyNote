
/**
 * @author phz
 * @desciption 第三方依赖包
 */
object ThirdPart {
    /**网络请求**/
    object Retrofit {//网路请求库retrofit
    private const val RETROFIT_VERSION = "2.9.0"
        const val retrofit = "com.squareup.retrofit2:retrofit:$RETROFIT_VERSION"

        //gson转换器
        const val convertGson = "com.squareup.retrofit2:converter-gson:$RETROFIT_VERSION"
        //scalars转换器
        const val convertScalars = "com.squareup.retrofit2:converter-scalars:$RETROFIT_VERSION"
        const val adapterRxjava2 = "com.squareup.retrofit2:adapter-rxjava2:$RETROFIT_VERSION"
        const val adapterRxjava3 = "com.squareup.retrofit2:adapter-rxjava3:$RETROFIT_VERSION"
    }

    object OkHttp {//okhttp
    private const val version = "4.8.0"
        const val okhttp = "com.squareup.okhttp3:okhttp:$version"
        const val urlConnection = "com.squareup.okhttp3:okhttp-urlconnection:$version"
        const val loggingInterceptor = "com.squareup.okhttp3:logging-interceptor:$version"
    }

    //用于持久化cookie
    //const val persistentCookieJar = "com.github.franmontiel:PersistentCookieJar:v1.0.1"

    //监听上传下载进度
    //const val progressManager = "me.jessyan:progressmanager:1.5.0"

    /**图片**/
    //图片加载框架
    object Glide {
        private const val version = "4.11.0"
        const val glide = "com.github.bumptech.glide:glide:$version"
        const val compiler = "com.github.bumptech.glide:compiler:$version"
    }
    //线圈（Kotlin 协程支持的 Android 图像加载库）
    const val coil="io.coil-kt:coil:1.3.2"

    /**播放器**/
    object ExoPlayer{
        private const val version="2.16.1"
        //完整依赖
        const val all="com.google.android.exoplayer:exoplayer:$version"
        //核心依赖
        const val core="com.google.android.exoplayer:exoplayer-core:$version"
        const val dash="com.google.android.exoplayer:exoplayer-dash:$version"
        const val hls="com.google.android.exoplayer:exoplayer-hls:$version"
        const val rtsp="com.google.android.exoplayer:exoplayer-rtsp:$version"
        const val smoothStreaming="com.google.android.exoplayer:exoplayer-smoothstreaming:$version"
        const val transformer="com.google.android.exoplayer:exoplayer-transformer:$version"
        const val ui="com.google.android.exoplayer:exoplayer-ui:$version"
    }

    /**BaiduMap**/
    object BaiduMap {
        private const val version = "7.4.0"
        private const val locationVersion = "9.1.8"
        private const val ttsVersion = "2.5.5"
        private const val panoramaVersion = "2.9.0"

        //地图组件
        const val map = "com.baidu.lbsyun:BaiduMapSDK_Map:$version"

        //步、骑行地图组件
        const val bwMap = "com.baidu.lbsyun:BaiduMapSDK_Map-BWNavi:$version"

        //驾车导航地图组件
        const val carMap = "com.baidu.lbsyun:BaiduMapSDK_Map-Navi:$version"

        //完整包地图组件（步、骑行+驾车导航）
        const val allMap = "com.baidu.lbsyun:BaiduMapSDK_Map-AllNavi:$version"

        //检索组件
        const val search = "com.baidu.lbsyun:BaiduMapSDK_Search:$version"

        //工具组件
        const val util = "com.baidu.lbsyun:BaiduMapSDK_Util:$version"

        //基础定位
        const val location = "com.baidu.lbsyun:BaiduMapSDK_Location:$locationVersion"

        //全量定位
        const val locationAll = "com.baidu.lbsyun:BaiduMapSDK_Location_All:$locationVersion"

        //TTS
        const val tts = "com.baidu.lbsyun:NaviTts:$ttsVersion"

        //全景
        const val panorama = "com.baidu.lbsyun:BaiduMapSDK_Panorama:$panoramaVersion"
    }

    /*******************************窗口、控件和相关工具***********************************/
    //插入即用的dialog
    //项目地址：https://github.com/afollestad/material-dialogs
    object MaterialDialogs {
        private const val version = "3.3.0"
        const val core = "com.afollestad.material-dialogs:core:$version"
        const val input = "com.afollestad.material-dialogs:input:$version"
        const val color = "com.afollestad.material-dialogs:color:$version"
        const val files = "com.afollestad.material-dialogs:files:$version"
        const val datetime = "com.afollestad.material-dialogs:datetime:$version"
        const val bottomSheets = "com.afollestad.material-dialogs:bottomsheets:$version"
        const val lifecycle = "com.afollestad.material-dialogs:lifecycle:$version"
    }

    //轮播图
    const val bannerVp = "com.github.zhpanvip:BannerViewPager:3.1.5"

    //状态栏
    const val immersionBar = "com.gyf.immersionbar:immersionbar:3.0.0"
    const val immersionBarKtx = "com.gyf.immersionbar:immersionbar-ktx:3.0.0"

    //上下拉刷新
    const val refreshLayoutKernel = "com.scwang.smart:refresh-layout-kernel:2.0.1"
    const val refreshHeader = "com.scwang.smart:refresh-header-classics:2.0.1"

    //RecycleView适配器工具
    const val baseRecycleViewHelper = "com.github.CymChad:BaseRecyclerViewAdapterHelper:3.0.4"

    //带侧滑菜单的列表
    const val xRecycleView = "com.yanzhenjie.recyclerview:x:1.3.2"

    //好用的指示器indicator
    const val magicIndicator = "com.github.hackware1993:MagicIndicator:1.7.0"

    //屏幕适配方案
    const val autoSize = "me.jessyan:autosize:1.2.1"

    //界面ui状态管理
    const val loadSir = "com.kingja.loadsir:loadsir:1.3.8"

    //通过标签直接写shape
    const val backgroundLibrary ="com.noober.background:core:1.6.5"

    //console
    const val console ="com.jraska:console:1.2.0"

    //lottie用来加载json动画
    // 你可以使用转换工具将mp4转换成json【https://isotropic.co/video-to-lottie/】
    // 去这里下载素材【https://lottiefiles.com/】
    const val lottie ="com.airbnb.android:lottie:4.1.0"

    /*******************************窗口、控件和相关工具***********************************/

    /*******************************依赖注入***********************************/
    object DI {
        object Koin {
            private const val koin_version = "2.1.5"

            const val core = "org.koin:koin-core:$koin_version}"
            const val android = "org.koin:koin-android:$koin_version"
            const val androidxViewModel = "org.koin:koin-androidx-viewmodel:$koin_version"
            const val androidScope = "org.koin:koin-android-scope:$$koin_version"
        }

        object Dagger{
            //tip:可搭配Hilt使用
            private const val version = "2.37"
            const val dagger ="com.google.dagger:dagger:$version"
            //use annotationProcessor ,not implementation
            const val compiler ="com.google.dagger:dagger-compiler::$version"
        }
    }
    /*******************************依赖注入***********************************/

    //常用运行时权限请求管理库
    const val rxPermission="com.github.tbruyelle:rxpermissions:0.12"

    object PermissionDispatcher{
        private const val version= "4.8.0"
        const val permissionsDispatcher="com.github.permissions-dispatcher:permissionsdispatcher:$version"
        //use kapt，not api or implementation
        const val processor="com.github.permissions-dispatcher:permissionsdispatcher-processor:$version"
    }

    const val xxPermission="com.github.getActivity:XXPermissions:12.5"

    //常用的工具类
    const val utilCodex = "com.blankj:utilcodex:1.30.0"

    //微信开源项目，替代SP
    const val mmkv = "com.tencent:mmkv:1.0.22"

    //rxjava2配合RxAndroid
    const val rxjava2 = "io.reactivex.rxjava2:rxjava:2.2.21"
    const val rxAndroid = "io.reactivex.rxjava2:rxandroid:2.1.1"
    //rxjava3配合RxAndroid
    /*const val rxjava3 = "io.reactivex.rxjava3:rxjava:3.0.13"
    const val rxAndroid3 = "io.reactivex.rxjava3:rxandroid:3.0.0"*/

    //腾讯bug上报收集
    const val bugly ="com.tencent.bugly:crashreport_upgrade:latest.release"

    //内存泄露检测库
    //use debugImplementation
    const val leakcanary="com.squareup.leakcanary:leakcanary-android:2.7"

    //BLE蓝牙操作工具
    const val rxAndroidBle ="com.polidea.rxandroidble2:rxandroidble:1.12.1"
    //Replaying share
    const val rxjavaReplayingShare ="com.jakewharton.rx2:replaying-share:2.2.0"

    //ARouter
    const val aRouter ="com.alibaba:arouter-api:1.5.2"
    const val aRouterCompiler ="com.alibaba:arouter-compiler:1.5.2"

    //Android-Iconics
    const val iconCore="com.mikepenz:iconics-core:5.3.1"
}