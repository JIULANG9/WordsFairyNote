package com.wordsfairy.note.ui.widgets

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.wordsfairy.note.ui.theme.AppColor
import com.wordsfairy.note.ui.theme.WordsFairyTheme

/**
 * @Description: Dialog
 * @Author: JIULANG
 * @Data: 2023/5/18 18:10
 */
/**
 * 普通的Dialog
 * @param title String
 * @param message String?
 */


@Composable
fun GeneralDialog(
    onDismissRequest: (() -> Unit)? = null,
    properties: DialogProperties = DialogProperties(),
    dialogState: MutableState<Boolean>,
    title: String,
    message: String? = null,
    isWaring:Boolean = false,
    positiveBtnText: String,
    onPositiveBtnClicked: () -> Unit = {},
    negativeBtnText: String? = null,
    onNegativeBtnClicked: () -> Unit = {  },
) {
    Dialog(
        onDismissRequest = {
            dialogState.value = false
            onDismissRequest?.invoke()
        },
        properties = properties,
    ) {
        GeneralDialogSkeleton(
            title = title,
            message = message,
            isWaring = isWaring,
            positiveBtnText = positiveBtnText,
            onPositiveBtnClicked = {
                dialogState.value = false

                onPositiveBtnClicked()
            },
            negativeBtnText = negativeBtnText,
            onNegativeBtnClicked = {
                dialogState.value = false

                onNegativeBtnClicked.invoke()
            },
        )
    }
}

@Composable
fun GeneralDialogSkeleton(
    title: String,
    message: String? = null,
    isWaring:Boolean = false,
    positiveBtnText: String,
    onPositiveBtnClicked: () -> Unit = {},
    negativeBtnText: String? = null,
    onNegativeBtnClicked: (() -> Unit)? = null,
) {
    Box(Modifier.fillMaxSize()) {
        Card(
            modifier = Modifier
                .align(Alignment.Center)
                .padding(bottom = 66.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = WordsFairyTheme.colors.dialogBackground)
        ) {

            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Text(
                    modifier = Modifier.padding(start = 16.dp, top = 16.dp, end = 16.dp),
                    text = title,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    fontSize = 20.sp,
                    color = WordsFairyTheme.colors.textPrimary
                )

                if (message != null) {
                    Text(
                        modifier = Modifier.padding(start = 16.dp, top = 16.dp, end = 16.dp),
                        text = message,
                        fontSize = 16.sp,
                        textAlign = TextAlign.Center,
                        lineHeight = 23.sp,
                        color = WordsFairyTheme.colors.textSecondary
                    )
                }

                Row {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 16.dp, top = 20.dp, end = 16.dp, bottom = 16.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (negativeBtnText != null) {
                            OutlinedButton(
                                modifier = Modifier
                                    .weight(.5f),
                                onClick = {
                                    onNegativeBtnClicked?.invoke()
                                }
                            ) {
                                Text(negativeBtnText,color = WordsFairyTheme.colors.textPrimary)
                            }

                            Spacer(modifier = Modifier.width(16.dp))
                        }

                        val buttonColor = if (isWaring) AppColor.red else AppColor.themeColor
                        Button(
                            modifier = Modifier
                                .weight(.5f),
                            colors = ButtonDefaults.buttonColors(containerColor = buttonColor),
                            onClick = {
                                onPositiveBtnClicked()
                            }
                        ) {
                            Text(positiveBtnText,color = WordsFairyTheme.colors.textWhite)
                        }
                    }
                }
            }
        }
    }
}
