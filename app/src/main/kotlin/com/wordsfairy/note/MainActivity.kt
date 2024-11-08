package com.wordsfairy.note

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.addCallback
import androidx.activity.compose.setContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*

import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.WindowCompat
import androidx.lifecycle.lifecycleScope
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
import com.wordsfairy.common.tools.AESEncryption
import com.wordsfairy.note.constants.EventBus
import com.wordsfairy.note.ext.flowbus.observeEvent
import com.wordsfairy.note.ui.page.home.HomePageScreen
import com.wordsfairy.note.ui.theme.WordsFairyNoteTheme
import com.wordsfairy.note.ui.widgets.toast.ToastModel
import com.wordsfairy.note.ui.widgets.toast.ToastUI
import com.wordsfairy.note.ui.widgets.toast.ToastUIState
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import kotlin.system.exitProcess


@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalAnimationApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        CONTEXT = this

        setContent {
            val navController = rememberAnimatedNavController()
            val toastState = remember { ToastUIState() }

            WordsFairyNoteTheme {
                Box(modifier = Modifier.fillMaxSize()) {
                    HomePageScreen(navController)
                    ToastUI(toastState)
                }
            }

            observeEvent(key = EventBus.NavController) {
                val route = it as String
                navController.navigate(route)
            }
            /** toast */
            observeEvent(key = EventBus.ShowToast) {
                lifecycleScope.launch {
                    val data = it as ToastModel
                    toastState.show(data)
                }
            }
        }

        onBackPressedDispatcher.addCallback {
            finish()
            exitProcess(0)
        }
    }

    companion object {
        @JvmStatic
        @SuppressLint("StaticFieldLeak")
        lateinit var CONTEXT: MainActivity
    }
}


