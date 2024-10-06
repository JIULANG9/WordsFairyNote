package com.wordsfairy.note.ui.theme

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.colorspace.ColorSpaces

val Purple80 = Color(0xFFD0BCFF)
val PurpleGrey80 = Color(0xFFCCC2DC)
val Pink80 = Color(0xFFEFB8C8)


val Transparent = Color(0x00000000)

val themeColor = Color(0xFF92E2c7)
val themeSecondaryColor = Color(0XFF92E2C7)

val white = Color(0xFFFFFFFF)
val white3 = Color(0xFFAE5E5E)

val black = Color(0xFF000000)

val statusBarColorLight = Color(0xFFFFFFFF)
val statusBarColorDark = Color(0xFF1C1C28)

val backgroundColorLight = Color(0xFFF2F5F9)
val backgroundColorDark = Color(0xFF1C1C28)
val backgroundSecondaryColorLight = Color(0xFFfbfcfd)
val backgroundSecondaryColorDark = Color(0xFF494953)
val themeBackground = Color(0xFFE7F2F1)
val whiteBackgroundColorLight = Color(0xFFFFFFFF)
val whiteBackgroundColorDark = Color(0xFF1C1C28)

val dialogBackgroundLight = Color(0xFFFEFEFE)
val dialogBackgroundDark = Color(0xFF2F323D)

val editBackgroundLight = Color(0xFFF2F3F8)
val editBackgroundDark = Color(0xFF707077)

val immerseBackgroundColorLight = Color(0xFFF2F3F8)
val immerseBackgroundColorDark = Color(0xFF0e0e14)

val itemBackgroundLight = Color(0xFFFFFFFF)
val itemBackgroundDark = Color(0xFFF33333D)

val textPrimaryLight = Color(0xFF333333)
val textPrimaryDark = Color(0xFFE8E8F0)

val textSecondaryLight = Color(0xFF999999)
val textSecondaryDark = Color(0xFFD5D5D5)

val textWhite = Color(0xFFFFFFFF)
val textBlack = Color(0xFF333333)

val blue = Color(0xFF51BDFF)
val blueLightAccent = Color(0xFFD0E7F8)

val blueDark = Color(0xFF2aa0fe)
val blueDarkAccent = Color(0xFF224B6F)

val red = Color(0xFFFF5500)
val red2 = Color(0xFFDD302E)
val green = Color(0xFF68be8d)
val grey = Color(0xFF888888)
val grey1 = Color(0xFF888888)
val themeAccentColor = Color(0xFFE7FBF7)


object AppColor {
    val blue = Color(0xFF51BDFF).convert(ColorSpaces.CieXyz)
    val red = Color(0xFFFF5500).convert(ColorSpaces.CieXyz)
    val themeAccent = Color(0xFFe9f9f4).convert(ColorSpaces.CieXyz)
    val themeColor = Color(0xFF92E2c7).convert(ColorSpaces.CieXyz)
    val warning = Color(0xFFDF7B00).convert(ColorSpaces.CieXyz)
}
