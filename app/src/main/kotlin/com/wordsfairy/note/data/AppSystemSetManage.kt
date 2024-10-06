package com.wordsfairy.note.data

import com.wordsfairy.base.utils.store.DataStoreUtils
import com.wordsfairy.note.constants.Constants

/**
 * @Description:
 * @Author: JIULANG
 * @Data: 2022/12/28 18:10
 */
object AppSystemSetManage {
    private const val InitialLoad = "initial_load"
    private const val ConsentAgreement_Key = "consent_agreement_key"

    private const val Dark_Mode = "dark_mode"
    private const val Close_Animation = "close_animation"
    private const val Dark_Mode_FOLLOW_SYSTEM = "dark_mode_follow_system"
    private const val SearchEnginesUrl = "search_engines_url_key"

    private const val JumpToWeChat = "jump_to_wechat"

    private const val HomeTabRememberPage = "home_tab_remember_page"

    //importAndDelete
    private const val ImportAndCover_Key = "import_and_cover"

    /**
     * 深色模式
     */
    var darkUI: Boolean
        get() = DataStoreUtils.readBooleanData(Dark_Mode, false)
        set(value) = DataStoreUtils.saveSyncBooleanData(Dark_Mode, value = value)

    fun setDarkMode(follow: Boolean) {
        darkUI = follow
    }

    /**
     * 关闭动画
     */
    var closeAnimation: Boolean
        get() = DataStoreUtils.readBooleanData(Close_Animation, false)
        set(value) = DataStoreUtils.saveSyncBooleanData(Close_Animation, value = value)

    /**
     * 深色模式跟随系统
     */
    var darkModeFollowSystem: Boolean
        get() = DataStoreUtils.readBooleanData(Dark_Mode_FOLLOW_SYSTEM, false)
        set(value) = DataStoreUtils.saveSyncBooleanData(Dark_Mode_FOLLOW_SYSTEM, value = value)

    fun followSystem(follow: Boolean) {
        darkModeFollowSystem = follow
    }

    /**
     * 搜索引擎
     */
    var searchEngines: String
        get() = DataStoreUtils.readStringData(SearchEnginesUrl, Constants.SearchEngines.Baidu)
        set(value) = DataStoreUtils.saveSyncStringData(SearchEnginesUrl, value = value)

    /**
     * 转跳微信
     */
    var jumpToWeChat: Boolean
        get() = DataStoreUtils.readBooleanData(JumpToWeChat, false)
        set(value) = DataStoreUtils.saveSyncBooleanData(JumpToWeChat, value = value)

    /**
     * 长文本自动折叠 默认开启
     */
    var longTextAutoFold: Boolean
        get() = DataStoreUtils.readBooleanData("long_text_auto_fold", true)
        set(value) = DataStoreUtils.saveSyncBooleanData("long_text_auto_fold", value = value)

    /**
     * 首页索引
     */
    var homeTabRememberPage: Int
        get() = DataStoreUtils.readIntData(HomeTabRememberPage, 0)
        set(value) = DataStoreUtils.saveSyncIntData(HomeTabRememberPage, value = value)

    /**
     * 初次加载
     */
    var initialLoad: Boolean
        get() = DataStoreUtils.readBooleanData(InitialLoad, false)
        set(value) = DataStoreUtils.saveSyncBooleanData(InitialLoad, value = value)

    /**
     * 同意协议
     */
    var consentAgreement: Boolean
        get() = DataStoreUtils.readBooleanData(ConsentAgreement_Key, false)
        set(value) = DataStoreUtils.saveSyncBooleanData(ConsentAgreement_Key, value = value)

    /**
     * 导入并删除
     * importAndDelete
     */
    var importAndCover: Boolean
        get() = DataStoreUtils.readBooleanData(ImportAndCover_Key, false)
        set(value) = DataStoreUtils.saveSyncBooleanData(ImportAndCover_Key, value = value)

}