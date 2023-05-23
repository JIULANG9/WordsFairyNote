package com.wordsfairy.note.ui.widgets


import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.geometry.toRect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathOperation
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
/**
 * @Description:
 * @Author: JIULANG
 * @Data: 2023/5/21 14:27
 */
@Composable
fun MaskedView(
    startPoint: Offset,
    endPoint: Offset,
    cornerRadius: Dp,
    maskColor: Color
) {
    val cornerRadiusPx = with(LocalDensity.current) { cornerRadius.toPx() }

    Box(modifier = Modifier.fillMaxSize()) {
        // 底部视图
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Transparent)
        )

        // 遮罩层
        Canvas(modifier = Modifier.fillMaxSize()) {
            // 创建圆角矩形遮罩
            val revealedPath = Path().apply {
                val roundRect = RoundRect(
                    left = startPoint.x,
                    top = startPoint.y,
                    right = endPoint.x,
                    bottom = endPoint.y,
                    topLeftCornerRadius = CornerRadius(cornerRadiusPx),
                    topRightCornerRadius = CornerRadius(cornerRadiusPx),
                    bottomLeftCornerRadius = CornerRadius(cornerRadiusPx),
                    bottomRightCornerRadius = CornerRadius(cornerRadiusPx)
                )
                addRoundRect(roundRect)
            }

            // 创建整个画布的矩形遮罩，与圆角矩形遮罩做差集操作
            val maskPath = Path().apply {
                addRect(size.toRect())
                op(this, revealedPath, PathOperation.Difference)
            }

            // 绘制遮罩
            drawPath(
                path = maskPath,
                color = maskColor
            )
        }
    }
}
