package com.wordsfairy.note.ui.widgets


import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.wordsfairy.note.ui.theme.WordsFairyTheme
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun AnimaSun(modifier: Modifier = Modifier, color: Color = WordsFairyTheme.colors.icon) {
    val transition = rememberInfiniteTransition()

    val animateTween by transition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(tween(5000), RepeatMode.Restart)
    )

    Canvas(modifier.rotate(animateTween)) {

        val radius = size.width / 6
        val stroke = size.width / 20
        val centerOffset = Offset(size.width / 30, size.width / 30)

        // draw circle
        drawCircle(
            color = color,
            radius = radius + stroke / 2,
            style = Stroke(width = stroke),
            center = center + centerOffset
        )
        drawCircle(
            color = Color.White,
            radius = radius,
            style = Fill,
            center = center + centerOffset

        )

        // draw line

        val lineLength = radius * 0.6f
        val lineOffset = radius * 1.8f
        (0..7).forEach { i ->

            val radians = Math.toRadians(i * 45.0)

            val offsetX = lineOffset * cos(radians).toFloat()
            val offsetY = lineOffset * sin(radians).toFloat()

            val x1 = size.width / 2 + offsetX + centerOffset.x
            val x2 = x1 + lineLength * cos(radians).toFloat()

            val y1 = size.height / 2 + offsetY + centerOffset.y
            val y2 = y1 + lineLength * sin(radians).toFloat()

            drawLine(
                color = color,
                start = Offset(x1, y1),
                end = Offset(x2, y2),
                strokeWidth = stroke,
                cap = StrokeCap.Round
            )
        }
    }
}

@Composable
fun Moon(modifier: Modifier = Modifier) {


    val color = WordsFairyTheme.colors.icon
    Canvas(modifier) {

        val canvasWidth = size.width  // 画布的宽
        val canvasHeight = size.height  // 画布的高

        val strokeWidth = size.width / 15
        val strokeHeight = size.height / 15

        // draw circle
        //外圆宽
        val outerWidth = canvasWidth - strokeWidth
        val outerHeight = canvasHeight - strokeHeight
        val shadowWidth = outerWidth * kotlin.math.sqrt(3.0) / 2
        val shadowHeight = outerHeight * kotlin.math.sqrt(3.0) / 2
        drawArc(
            color = color,
            startAngle = 40F,
            sweepAngle = 240f,
            useCenter = false,
            style = Stroke(
                width = strokeWidth
            ),
            topLeft = Offset(strokeWidth / 2, strokeHeight / 2),
            size = Size(outerWidth, outerHeight),
        )
        drawArc(
            color = color,
            startAngle = 70F,
            sweepAngle = 180f,
            useCenter = false,
            style = Stroke(width = strokeWidth),
            topLeft = Offset((strokeWidth) + canvasWidth / 4, strokeHeight / 4),
            size = Size(shadowWidth.toFloat(), shadowHeight.toFloat()),
        )
    }
}

@Composable
fun AnimateContent(
    content: @Composable ColumnScope.() -> Unit,
    expandContent: @Composable ColumnScope.() -> Unit,
    verticalPadding: Dp = 6.dp,
    horizontalPadding: Dp = 6.dp,
    onClick: () -> Unit = {}
) {
    var expand by remember { mutableStateOf(false) }
    Column(
        modifier = Modifier
            .padding(vertical = verticalPadding, horizontal = horizontalPadding)
            .wrapContentSize()
            .animateContentSize(
                tween(500)
            ),
    ) {

        ImmerseCard {
            Column(content = content, modifier = Modifier.clickable {
                onClick.invoke()
                expand = !expand
            })
        }

        if (expand) {
            Column(
                content = expandContent,
                horizontalAlignment = Alignment.CenterHorizontally
            )
        }
    }
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun SlideAnimatedNavHost(
    navController: NavHostController,
    startDestination: String,
    builder: NavGraphBuilder.() -> Unit
) {
    AnimatedNavHost(
        navController,
        startDestination = startDestination,
        enterTransition = {
            slideInHorizontally(
                initialOffsetX = { fullWidth -> fullWidth },
                animationSpec = tween(600)
            )
        },
        exitTransition = {
            slideOutHorizontally(
                targetOffsetX = { fullWidth -> -fullWidth },
                animationSpec = tween(600)
            )
        },
        popEnterTransition = {
            slideInHorizontally(
                initialOffsetX = { fullWidth -> -fullWidth },
                animationSpec = tween(600)
            )
        },
        popExitTransition = {
            slideOutHorizontally(
                targetOffsetX = { fullWidth -> fullWidth },
                animationSpec = tween(600)
            )
        }, builder = builder
    )
}

@Composable
fun VisibilityView(
    visible: Boolean,
    content: @Composable() AnimatedVisibilityScope.() -> Unit
) {
    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(),
        exit = fadeOut(), content = content
    )
}


@Preview
@Composable
fun PreviewAnimatableSun() {
    Moon()
}