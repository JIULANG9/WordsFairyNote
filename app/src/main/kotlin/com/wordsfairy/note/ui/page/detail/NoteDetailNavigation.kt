package com.wordsfairy.note.ui.page.detail

import android.annotation.SuppressLint
import android.util.Log
import androidx.annotation.VisibleForTesting
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.ColumnScope
import androidx.lifecycle.SavedStateHandle
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.NavGraphBuilder
import com.google.accompanist.navigation.animation.composable
import androidx.navigation.navArgument
import com.wordsfairy.note.constants.EventBus
import com.wordsfairy.note.constants.GlobalData
import com.wordsfairy.note.constants.NavigateRouter
import com.wordsfairy.note.data.entity.NoteEntity
import com.wordsfairy.note.ext.flowbus.postEventValue
import java.net.URLDecoder


@VisibleForTesting
internal const val Detail_Is_Search = "detailIsSearch"

fun ColumnScope.toNoteDetailsUI(it: NoteEntity, isSearch: Boolean) {
    GlobalData.noteDetailsNoteEntity = it
    postEventValue(EventBus.NavController, NavigateRouter.DetailPage.Detail + "/$isSearch")
}
@ExperimentalAnimationApi
fun NavGraphBuilder.noteDetailScreen(
    navController: NavHostController
) {
    composable(
        route = NavigateRouter.DetailPage.Detail + "/{$Detail_Is_Search}",
        arguments = listOf(
            navArgument(Detail_Is_Search) {
                type = NavType.BoolType   //指定具体类型
                defaultValue = false //默认值（选配）
                nullable = false    //可否为null（选配）
            }
        ),
    ) { backStackEntry ->
        // 这里可以传参数，通过it.arguments获取
        val isSearch = backStackEntry.arguments?.getBoolean(Detail_Is_Search)
        NoteDetailsUI(onBack = {
            navController.navigateUp()
        },  isSearch?:false)
    }
}
