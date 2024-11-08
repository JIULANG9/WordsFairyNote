package com.wordsfairy.note.ui.page.detail.wifgets

import androidx.compose.foundation.layout.*
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import com.wordsfairy.note.data.entity.NoteContentEntity
import com.wordsfairy.note.ui.theme.AppResId
import com.wordsfairy.note.ui.theme.WordsFairyTheme
import com.wordsfairy.note.ui.widgets.CancelButton
import com.wordsfairy.note.ui.widgets.ConfirmButton
import com.wordsfairy.note.ui.widgets.dropdown.AnimatedSlideFormBottom
import kotlinx.coroutines.delay

/**
 * @Description:
 * @Author: JIULANG
 * @Data: 2023/5/12 15:36
 */
@ExperimentalMaterial3Api
@ExperimentalComposeUiApi
@Composable
fun ModifyContentDialog(
    isVisible: Boolean,
    entity: NoteContentEntity?,
    onDismiss: () -> Unit,
    onConfirm: (NoteContentEntity) -> Unit,
) {
    if (entity == null) {
        return
    }

    var textFieldValue by remember {
        mutableStateOf(TextFieldValue())
    }

    LaunchedEffect(entity.hashCode()) {
        textFieldValue = TextFieldValue(
            text = entity.content,
            selection = TextRange(entity.content.length)
        )
    }
    //错误
    var isError by rememberSaveable { mutableStateOf(false) }
    val focusRequester = remember { FocusRequester() }
    val keyboardController = LocalSoftwareKeyboardController.current
    AnimatedSlideFormBottom(isVisible, onDismiss = {
        onDismiss.invoke()
        isError = false
        focusRequester.freeFocus()
    }) {

        LaunchedEffect(Unit) {
            delay(300)
            focusRequester.requestFocus()
        }
        Column(
            Modifier
                .padding(horizontal = 26.dp, vertical = 16.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            OutlinedTextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .focusRequester(focusRequester)
                    .onFocusChanged {
                        if (it.isFocused) {
                            keyboardController?.show()
                        }
                    },
                value = textFieldValue,
                onValueChange = {
                    textFieldValue = it
                },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = WordsFairyTheme.colors.themeUi,
                    focusedTextColor = WordsFairyTheme.colors.textPrimary
                ),
                label = { Text(stringResource(id = AppResId.String.NoteContentText)) },
                isError = isError
            )
            Spacer(Modifier.height(16.dp))
            Row(
                Modifier
                    .fillMaxWidth()
            ) {
                /** 取消按钮 */
                CancelButton(Modifier.weight(1f)) {
                    isError = false
                    textFieldValue = TextFieldValue()
                    onDismiss.invoke()
                    focusRequester.freeFocus()
                }
                Spacer(Modifier.width(12.dp))
                /** 确认按钮 */
                val feedback = LocalHapticFeedback.current
                ConfirmButton(Modifier.weight(1f)) {
                    if (textFieldValue.text.isEmpty()) {
                        isError = true
                    } else {
                        isError = false

                        entity.content = textFieldValue.text
                        onConfirm.invoke(entity)
                        onDismiss.invoke()
                    }
                    //震动
                    feedback.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                    focusRequester.freeFocus()
                    textFieldValue = TextFieldValue()
                }
            }
        }
    }
}