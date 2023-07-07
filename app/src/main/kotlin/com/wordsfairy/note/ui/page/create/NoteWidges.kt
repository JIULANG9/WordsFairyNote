package com.wordsfairy.note.ui.page.create

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.*
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.wordsfairy.note.data.entity.NoteContentEntity
import com.wordsfairy.note.ui.common.click
import com.wordsfairy.note.ui.common.vibration
import com.wordsfairy.note.ui.theme.AppColor
import com.wordsfairy.note.ui.theme.AppResId
import com.wordsfairy.note.ui.theme.WordsFairyTheme
import com.wordsfairy.note.ui.widgets.*
import com.wordsfairy.note.ui.widgets.dropdown.AnimatedVisibilitySlide
import kotlinx.coroutines.delay

/**
 * @Description:
 * @Author: JIULANG
 * @Data: 2023/4/29 0:13
 */

@Composable
fun ChooseClassifyButton(
    text: String?,
    longClick: () -> Unit = {},
    onClick: () -> Unit,
) {
    val folderName = text ?: "未分类"

    val containerColor =
        if (text == null) WordsFairyTheme.colors.immerseBackground else WordsFairyTheme.colors.themeUi.copy(
            alpha = 0.3f
        )
    val textColor = WordsFairyTheme.colors.textPrimary

    Button(
        onClick = onClick,
        contentPadding = ButtonDefaults.ButtonWithIconContentPadding,
        colors = ButtonDefaults.buttonColors(containerColor = containerColor)
    ) {
        Image(
            modifier = Modifier.click { longClick.invoke() },
            painter = painterResource(AppResId.Drawable.Folder),
            contentDescription = text
        )
        Spacer(Modifier.size(ButtonDefaults.IconSpacing))
        Text(folderName, color = textColor)
    }
}

/** 添加笔记文件夹名称 */
@OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class)
@Composable
fun AddFolderDialog(
    isVisible: Boolean,
    text: String,
    onValueChange: (String) -> Unit,
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit,
) {
    //错误
    var isError by rememberSaveable { mutableStateOf(false) }
    val focusRequester = remember { FocusRequester() }
    val keyboardController = LocalSoftwareKeyboardController.current
    val feedback = LocalHapticFeedback.current

    AnimatedVisibilitySlide(isVisible, onDismiss = {
        onDismiss.invoke()
        isError = false
        focusRequester.freeFocus()
    }) {

        LaunchedEffect(Unit) {
            delay(500)
            focusRequester.requestFocus()
        }
        Column(
            Modifier
                .padding(horizontal = 26.dp, vertical = 16.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Title(title = stringResource(id = AppResId.String.CreateFolder), fontSize = 21.sp)
            Spacer(Modifier.height(6.dp))
            OutlinedTextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .focusRequester(focusRequester)
                    .onFocusChanged {
                        if (it.isFocused) {
                            keyboardController?.show()
                        }
                    },
                value = text,
                onValueChange = onValueChange,
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    focusedBorderColor = WordsFairyTheme.colors.themeUi,
                    textColor = WordsFairyTheme.colors.textPrimary
                ),
                label = { Text(stringResource(id = AppResId.String.FolderName)) },
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
                    onDismiss.invoke()
                    focusRequester.freeFocus()
                }
                Spacer(Modifier.width(12.dp))
                /** 确认按钮 */
                ConfirmButton(Modifier.weight(1f)) {
                    if (text.isEmpty()) {
                        isError = true
                    } else {
                        isError = false
                        onConfirm.invoke(text)
                        onDismiss.invoke()
                    }
                    //震动
                    feedback.vibration()
                    focusRequester.freeFocus()
                }
            }
        }
    }
}

@Composable
fun ButtonSaveTitle(
    expanded: Boolean = false,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    AnimatedVisibility(
        expanded,
        modifier
    ) {
        MyIconButton(imageVector = Icons.Rounded.Check) {
            onClick.invoke()
        }
    }
}


@ExperimentalComposeUiApi
@Composable
fun CreateNoteEditView(
    text: String,
    placeholder: String,
    keyboardType: KeyboardType = KeyboardType.Text,
    onValueChange: (String) -> Unit = {},
) {
    TextInputFieldOne(
        modifier = Modifier.padding(horizontal = 12.dp),
        text = text,
        placeholder = placeholder,
        background = Color.Transparent,
        keyboardType = keyboardType,
        fontSize = 21.sp,
        imeAction = ImeAction.Next,
        textFontWeight = FontWeight.Bold,
        onValueChange = onValueChange
    )
}

@ExperimentalComposeUiApi
@Composable
fun CreateNoteContentEditView(
    text: String,
    addendText: String,
    placeholder: String,
    isAutoFocused: Boolean = true,
    keyboardType: KeyboardType = KeyboardType.Text,
    onValueChange: (String) -> Unit = {},
) {

    var textFieldValue by remember { mutableStateOf(TextFieldValue()) }

    LaunchedEffect(addendText) {
        textFieldValue = TextFieldValue(
            text = textFieldValue.text + addendText,
            selection = TextRange(textFieldValue.text.length + addendText.length)
        )

        onValueChange.invoke(textFieldValue.text)
    }
    LaunchedEffect(text) {
        textFieldValue = textFieldValue.copy(text = text)
    }

    TextFieldInputField(
        modifier = Modifier,
        textField = textFieldValue,
        placeholder = placeholder,
        background = WordsFairyTheme.colors.editBackground,
        keyboardType = keyboardType,
        fontSize = 16.sp,
        imeAction = ImeAction.Default,
        singleLine = false,
        isAutoFocused = isAutoFocused,
        maxLines = 19,
        onValueChange = {
            textFieldValue = it
            onValueChange.invoke(it.text)
        }
    )
}

@Composable
fun NoteContentLazyColumn(noteContentItems: List<NoteContentEntity>) {
    LazyColumn(
        Modifier
            .fillMaxWidth()
            .animateContentSize()
    ) {
        itemsIndexed(items = noteContentItems) { index, item ->
            ImmerseCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 2.dp),
                shape = RoundedCornerShape(6.dp),
                backgroundColor = WordsFairyTheme.colors.itemImmerse,
            ) {
                Box(Modifier.padding(9.dp)) {
                    TextNoteContent(text = item.content, color = WordsFairyTheme.colors.textPrimary)
                }
            }
        }
    }
}
