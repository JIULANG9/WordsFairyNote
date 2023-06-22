package com.wordsfairy.note.constants

/**
 * @Description:
 * @Author: JIULANG
 * @Data: 2023/4/24 11:52
 */
object NavigateRouter {


    object HomePage {
        /** 首页 */
        private const val ROUTER_GROUP_HOME = "homepage"

        const val HOME = "$ROUTER_GROUP_HOME/home"
        const val FolderManage = "$ROUTER_GROUP_HOME/home/FolderManage"
    }
    object SetPage {
        /** 全局设置 */
        private const val ROUTER_GROUP_SET = "set_page"

        const val Set = "$ROUTER_GROUP_SET/home"
        const val NoteData = "$ROUTER_GROUP_SET/notedata"
        const val BackupsProgressBar = "$ROUTER_GROUP_SET/notedata/backupsprogressbar"
        const val BackupsQRCode = "$ROUTER_GROUP_SET/notedata/backupsqrcode"

    }
    object DetailPage {
        /** 笔记详细 */
        private const val ROUTER_GROUP_SET = "detail_page"

        const val Detail = "$ROUTER_GROUP_SET/detail"
        const val Set = "$ROUTER_GROUP_SET/set"
        const val ProgressBarUI = "$ROUTER_GROUP_SET/progressbar_ui"
    }
}