package com.wordsfairy.note

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.addCallback
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
import com.wordsfairy.note.constants.EventBus
import com.wordsfairy.note.ext.flowbus.observeEvent
import com.wordsfairy.note.ui.page.home.HomePageScreen
import com.wordsfairy.note.ui.theme.WordsFairyTheme
import com.wordsfairy.note.ui.theme.WordsFairyNoteTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlin.system.exitProcess


@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val mViewModel: MainViewModel by viewModels()

    @OptIn(ExperimentalAnimationApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        CONTEXT = this

        setContent {
            val navController = rememberAnimatedNavController()
            WordsFairyNoteTheme() {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = WordsFairyTheme.colors.background
                ) {
                    HomePageScreen(navController)
                }
            }
            observeEvent(key = EventBus.NavController) {
                val route = it as String
                navController.navigate(route)
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
