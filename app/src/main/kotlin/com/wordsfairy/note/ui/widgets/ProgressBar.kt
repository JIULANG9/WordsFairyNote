package com.wordsfairy.note.ui.widgets

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.wordsfairy.note.ui.theme.WordsFairyTheme
import java.text.DecimalFormat



import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.*
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
/**
 * @Description:
 * @Author: JIULANG
 * @Data: 2023/5/17 11:17
 */
@Composable
fun IndicatorComponent(
    foregroundSweepAngle: Float,
    componentSize: Dp = 300.dp,
    maxIndicatorNum: Float = 100f,
    indicatorColor: Color = WordsFairyTheme.colors.themeUi,
    backgroundIndicatorColor: Color = WordsFairyTheme.colors.themeUi.copy(alpha = 0.2f),
    backgroundIndicatorStrokeWidth: Float = 100f,

) {

    val targetIndicatorValue = remember {
        Animatable(initialValue = 0f)
    }

    val legalSweepAngle = foregroundSweepAngle in 0.0..maxIndicatorNum.toDouble()

    LaunchedEffect(key1 = foregroundSweepAngle, block = {
        if (legalSweepAngle) {
            targetIndicatorValue.animateTo(
                targetValue = foregroundSweepAngle * 2.4f
            )
        }
    })

    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .size(componentSize)
            .drawBehind {
                val indicatorComponentSize = size * 0.8f
                drawBackgroundIndicator(
                    componentSize = indicatorComponentSize,
                    stroke = backgroundIndicatorStrokeWidth,
                    color = backgroundIndicatorColor
                )
                drawForegroundIndicator(
                    componentSize = indicatorComponentSize,
                    stroke = backgroundIndicatorStrokeWidth,
                    sweepAngle = targetIndicatorValue.value,
                    color = indicatorColor,
                )

            }
    ) {
        Text(
            text = "当前进度",
            style = TextStyle(
                color = WordsFairyTheme.colors.textSecondary,
                fontSize = 16.sp
            )
        )
        //保留两位小数
        val format = DecimalFormat("0.00")
        val schedule = format.format(foregroundSweepAngle)
        Text(
            text = "$schedule%",
            Modifier.padding(start = 3.dp) ,
            style = TextStyle(
                color = WordsFairyTheme.colors.textPrimary,
                fontSize = 36.sp,
                fontWeight = FontWeight.Bold
            )
        )

    }
}


fun DrawScope.drawBackgroundIndicator(componentSize: Size, stroke: Float, color: Color) {
    drawArc(
        size = componentSize,
        color = color,
        startAngle = 150f,
        sweepAngle = 240f,
        useCenter = false,
        style = Stroke(cap = StrokeCap.Round, width = stroke),
        topLeft = Offset(
            x = ((size.width - componentSize.width) / 2),
            y = ((size.height - componentSize.height) / 2)
        )
    )
}


fun DrawScope.drawForegroundIndicator(
    componentSize: Size,
    stroke: Float,
    sweepAngle: Float,
    color :Color
) {

    /**
     brush = Brush.sweepGradient(
    0.111f to Color(0xFF4286f4),
    0.388f to Color(0xFF373B44),
    1f to Color(0xFF4286f4)
    ),
    * */
    drawArc(
        size = componentSize,
        color = color,
        startAngle = 150f,
        sweepAngle = sweepAngle,
        useCenter = false,
        style = Stroke(cap = StrokeCap.Round, width = stroke),
        topLeft = Offset(
            x = ((size.width - componentSize.width) / 2),
            y = ((size.height - componentSize.height) / 2)
        )
    )
}