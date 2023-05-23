package com.wordsfairy.note.ui.page.launcher

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.addCallback
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.core.view.WindowCompat
import com.wordsfairy.note.MainActivity
import com.wordsfairy.note.data.AppSystemSetManage
import com.wordsfairy.note.ui.theme.WordsFairyNoteTheme
import com.wordsfairy.note.ui.theme.WordsFairyTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlin.system.exitProcess


/**
 * @Description:
 * @Author: JIULANG
 * @Data: 2023/5/21 10:50
 */


@AndroidEntryPoint
class LauncherActivity : ComponentActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

            WindowCompat.setDecorFitsSystemWindows(window, false)


        onBackPressedDispatcher.addCallback {
            finish()
//            exitProcess(0)
        }
    }
}
