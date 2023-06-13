package com.wordsfairy.note.ui.theme

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.TweenSpec
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.map
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.wordsfairy.note.data.AppSystemSetManage
import com.wordsfairy.note.ext.observeAsState
import com.wordsfairy.note.ui.theme.AppColor.themeAccent

private val LightColorScheme = WordsFairyColors(
    statusBarColor = statusBarColorLight,
    navigationBarColor = white,
    themeUi = themeColor,
    themeAccent = themeAccent,
    background = backgroundColorLight,
    backgroundSecondary = backgroundSecondaryColorLight,
    whiteBackground = whiteBackgroundColorLight,
    immerseBackground = immerseBackgroundColorLight,
    dialogBackground = dialogBackgroundLight,
    itemBackground = itemBackgroundLight,
    itemImmerse = immerseBackgroundColorLight,
    editBackground = editBackgroundLight,
    textPrimary = textPrimaryLight,
    textSecondary = textSecondaryLight,
    textWhite = textWhite,
    textBlack = textBlack,
    selectColor = themeAccentColor,
    icon = grey,
    iconBlack = black,
    success = green,
    info = blue,
    error = red2,
    btnBgPrimary = themeColor,
    btnBgSecond = themeColor,
    placeholder = white3,

    )

private val DarkColorScheme = WordsFairyColors(
    statusBarColor = statusBarColorDark,
    navigationBarColor = black,
    themeUi = themeColor,
    themeAccent = themeAccent,
    background = backgroundColorDark,
    backgroundSecondary = backgroundSecondaryColorDark,
    whiteBackground = whiteBackgroundColorDark,
    immerseBackground = immerseBackgroundColorDark,
    dialogBackground = dialogBackgroundDark,
    itemBackground = itemBackgroundDark,
    itemImmerse = itemBackgroundDark,
    editBackground = editBackgroundDark,
    textPrimary = textPrimaryDark,
    textSecondary = textSecondaryDark,
    textWhite = textWhite,
    textBlack = textBlack,
    selectColor = themeAccentColor,
    icon = grey,
    iconBlack = grey,
    success = green,
    info = blueDark,
    error = red2,
    btnBgPrimary = themeColor,
    btnBgSecond = themeColor,
    placeholder = grey1,
)


@Stable
object WordsFairyTheme {
    val colors: WordsFairyColors
        @Composable
        get() = LocalAppColors.current

    enum class Theme {
        Light, Dark
    }
}

@Stable
class WordsFairyColors(
    statusBarColor: Color,
    navigationBarColor: Color,
    themeUi: Color,
    themeAccent: Color,
    background: Color,
    backgroundSecondary: Color,
    whiteBackground: Color,
    immerseBackground: Color,
    dialogBackground: Color,
    itemBackground: Color,
    itemImmerse: Color,
    editBackground: Color,
    textPrimary: Color,
    textSecondary: Color,
    textWhite: Color,
    textBlack: Color,
    selectColor: Color,
    icon: Color,
    iconBlack: Color,
    success: Color,
    info: Color,
    error: Color,
    btnBgPrimary: Color,
    btnBgSecond: Color,
    placeholder: Color,
) {
    var statusBarColor: Color by mutableStateOf(statusBarColor)
        internal set
    var navigationBarColor: Color by mutableStateOf(navigationBarColor)
        internal set

    var themeUi: Color by mutableStateOf(themeUi)
        internal set
    var themeAccent: Color by mutableStateOf(themeAccent)
        internal set
    var background: Color by mutableStateOf(background)
        internal set
    var backgroundSecondary: Color by mutableStateOf(backgroundSecondary)
        private set
    var whiteBackground: Color by mutableStateOf(whiteBackground)
        private set
    var immerseBackground: Color by mutableStateOf(immerseBackground)
        private set
    var dialogBackground: Color by mutableStateOf(dialogBackground)
        private set
    var itemBackground: Color by mutableStateOf(itemBackground)
        private set
    var itemImmerse: Color by mutableStateOf(itemImmerse)
        private set
    var editBackground: Color by mutableStateOf(editBackground)
        private set
    var textPrimary: Color by mutableStateOf(textPrimary)
        internal set
    var textSecondary: Color by mutableStateOf(textSecondary)
        private set
    var textWhite: Color by mutableStateOf(textWhite)
        private set
    var textBlack: Color by mutableStateOf(textBlack)
        private set
    var selectColor: Color by mutableStateOf(selectColor)
        private set
    var icon: Color by mutableStateOf(icon)
        private set
    var iconBlack: Color by mutableStateOf(iconBlack)
        private set
    var success: Color by mutableStateOf(success)
        private set
    var info: Color by mutableStateOf(info)
        private set
    var error: Color by mutableStateOf(error)
        private set
    var primaryBtnBg: Color by mutableStateOf(btnBgPrimary)
        internal set
    var secondBtnBg: Color by mutableStateOf(btnBgSecond)
        private set
    var placeholder: Color by mutableStateOf(placeholder)
        private set
}

var LocalAppColors = compositionLocalOf {
    LightColorScheme
}

object WordsFairyThemeLiveData : MutableLiveData<WordsFairyTheme.Theme>()
object FollowSystemLiveData : MutableLiveData<Boolean>()

/** 未读通知 */
private val themeChanges: LiveData<WordsFairyTheme.Theme> = WordsFairyThemeLiveData.map {
    it
}
private val followSystemChanges: LiveData<Boolean> = FollowSystemLiveData.map {
    it
}


@Composable
fun WordsFairyNoteTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val currentTheme = if (AppSystemSetManage.darkUI) WordsFairyTheme.Theme.Dark else WordsFairyTheme.Theme.Light
    val theme by themeChanges.observeAsState(currentTheme)
    val followSystem by followSystemChanges.observeAsState(AppSystemSetManage.darkModeFollowSystem)

    val targetColors = if (followSystem){
        if (darkTheme) DarkColorScheme else LightColorScheme
    }else{
        when (theme) {
            WordsFairyTheme.Theme.Light -> LightColorScheme
            WordsFairyTheme.Theme.Dark -> DarkColorScheme
        }
    }

    val darkThemeMode = when (theme) {
        WordsFairyTheme.Theme.Light -> false
        WordsFairyTheme.Theme.Dark -> true
    }

    val statusBarColor = animateColorAsState(targetColors.statusBarColor, TweenSpec(600))
    val navigationBarColor = animateColorAsState(targetColors.background, TweenSpec(600))

    val themeUi = animateColorAsState(targetColors.themeUi, TweenSpec(600))
    val themeAccent = animateColorAsState(targetColors.themeAccent, TweenSpec(600))
    val background = animateColorAsState(targetColors.background, TweenSpec(600))
    val backgroundSecondary = animateColorAsState(targetColors.backgroundSecondary, TweenSpec(600))

    val whiteBackground = animateColorAsState(targetColors.whiteBackground, TweenSpec(600))
    val immerseBackground = animateColorAsState(targetColors.immerseBackground, TweenSpec(600))
    val dialogBackground = animateColorAsState(targetColors.dialogBackground, TweenSpec(600))
    val itemBackground = animateColorAsState(targetColors.itemBackground, TweenSpec(600))
    val itemImmerse = animateColorAsState(targetColors.itemImmerse, TweenSpec(600))
    val editBackground = animateColorAsState(targetColors.editBackground, TweenSpec(600))

    val textPrimary = animateColorAsState(targetColors.textPrimary, TweenSpec(600))
    val textSecondary = animateColorAsState(targetColors.textSecondary, TweenSpec(600))
    val textWhite = animateColorAsState(targetColors.textWhite, TweenSpec(600))
    val textBlack = animateColorAsState(targetColors.textBlack, TweenSpec(600))

    val selectColor = animateColorAsState(targetColors.selectColor, TweenSpec(600))

    val icon = animateColorAsState(targetColors.icon, TweenSpec(600))
    val iconBlack = animateColorAsState(targetColors.iconBlack, TweenSpec(600))

    val success = animateColorAsState(targetColors.success, TweenSpec(600))
    val info = animateColorAsState(targetColors.info, TweenSpec(600))
    val error = animateColorAsState(targetColors.error, TweenSpec(600))
    val primaryBtnBg = animateColorAsState(targetColors.primaryBtnBg, TweenSpec(600))
    val secondBtnBg = animateColorAsState(targetColors.secondBtnBg, TweenSpec(600))
    val placeholder = animateColorAsState(targetColors.placeholder, TweenSpec(600))


    val appColors = WordsFairyColors(
        statusBarColor = statusBarColor.value,
        navigationBarColor = navigationBarColor.value,
        themeUi = themeUi.value,
        themeAccent = themeAccent.value,
        background = background.value,
        backgroundSecondary = backgroundSecondary.value,

        whiteBackground = whiteBackground.value,
        immerseBackground = immerseBackground.value,
        dialogBackground = dialogBackground.value,
        itemBackground = itemBackground.value,
        itemImmerse = itemImmerse.value,
        editBackground = editBackground.value,

        textPrimary = textPrimary.value,
        textSecondary = textSecondary.value,
        textWhite = textWhite.value,
        textBlack = textBlack.value,

        selectColor = selectColor.value,

        icon = icon.value,
        iconBlack = iconBlack.value,
        btnBgPrimary = primaryBtnBg.value,
        btnBgSecond = secondBtnBg.value,
        success = success.value,
        info = info.value,
        error = error.value,
        placeholder = placeholder.value
    )

    CompositionLocalProvider(LocalAppColors provides appColors) {

        val systemUiController = rememberSystemUiController()
        val statusBarColor = Color.Transparent
        val navigationColor = appColors.navigationBarColor
        SideEffect {
            systemUiController.setStatusBarColor(statusBarColor, darkIcons = !darkThemeMode)
            systemUiController.setNavigationBarColor(navigationColor, darkIcons = !darkThemeMode)
        }

        val colors = if (darkTheme) {
            darkColorScheme(
                primary = appColors.themeUi,
                onPrimary =appColors.textWhite
            )
        } else {
            lightColorScheme(primary = appColors.themeUi)
        }

        MaterialTheme(
            colorScheme = colors,
            shapes = MyShapes,
            content = content,
            typography = Typography,
        )
    }
}



