package com.wordsfairy.note.ui.widgets

import com.wordsfairy.note.R
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.relocation.BringIntoViewRequester
import androidx.compose.foundation.relocation.bringIntoViewRequester
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.*

import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.*
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.LocalTextInputService
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.*
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.*

import com.google.accompanist.insets.LocalWindowInsets
import com.wordsfairy.note.ui.theme.AppResId

import com.wordsfairy.note.ui.theme.WordsFairyTheme
import com.wordsfairy.note.ui.theme.H5
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@ExperimentalMaterial3Api
@Composable
fun SearchEditView(
    text: String,
    hintText: String,
    modifier: Modifier = Modifier,
    onValueChanged: (String) -> Unit,
    onDeleteClick: () -> Unit,
    onSearch: () -> Unit,

    ) {
    val keyboardService = LocalTextInputService.current
    val editFocusManager = LocalFocusManager.current


    val focusRequester = remember { FocusRequester() }
    val keyboardController = LocalSoftwareKeyboardController.current

    LaunchedEffect(Unit) {
        delay(300)
        focusRequester.requestFocus()
    }

    TextField(
        modifier = modifier
            .fillMaxWidth()
            .focusRequester(focusRequester)
            .onFocusChanged {
                if (it.isFocused) {
                    keyboardController?.show()
                }
            }
            .padding(vertical = 3.dp, horizontal = 12.dp),
        value = text,
        textStyle = TextStyle.Default.copy(fontSize = 16.sp),
        onValueChange = { onValueChanged.invoke(it) },
        placeholder = {
            TextContent(hintText)
        },
        colors = TextFieldDefaults.colors(
            focusedTextColor = WordsFairyTheme.colors.textPrimary,
            focusedContainerColor = WordsFairyTheme.colors.itemBackground,
            unfocusedContainerColor = WordsFairyTheme.colors.itemBackground,
            focusedIndicatorColor = Color.Transparent, // 有焦点时的颜色，透明
            unfocusedIndicatorColor = Color.Transparent, // 无焦点时的颜色，绿色
        ),
        trailingIcon = {

            val textIsNotEmpty = text.isNotEmpty()
            AnimatedVisibility(
                visible = textIsNotEmpty,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                MyIconButton(Icons.Default.Clear) {
                    onDeleteClick()
                }
            }

            AnimatedVisibility(
                visible = !textIsNotEmpty,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                Icon(
                    painter = painterResource(id = AppResId.Drawable.Search),
                    contentDescription = null,
                    tint = WordsFairyTheme.colors.icon,
                    modifier = Modifier.size(24.dp)
                )
            }

        },
        shape = RoundedCornerShape(36.dp),
        keyboardActions = KeyboardActions {
            editFocusManager.clearFocus()
            onSearch.invoke()
        },
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Text,
            imeAction = ImeAction.Search
        )
    )
}

@ExperimentalMaterial3Api
@Composable
fun CommentEditView(
    text: String,
    hintText: String,
    modifier: Modifier = Modifier,
    editFocusRequester: FocusRequester,
    onValueChanged: (String) -> Unit,
    onDeleteClick: () -> Unit,
    onSend: () -> Unit,

    ) {
    val keyboardService = LocalTextInputService.current
    val editFocusManager = LocalFocusManager.current

    TextField(
        modifier = modifier
            .fillMaxWidth()
            .focusRequester(editFocusRequester)
            .padding(vertical = 9.dp, horizontal = 16.dp),
        value = text,
        textStyle = TextStyle.Default.copy(fontSize = H5),
        onValueChange = { onValueChanged.invoke(it) },
        placeholder = {
            TextContent(hintText)
        },
        colors = TextFieldDefaults.colors(
            focusedTextColor = WordsFairyTheme.colors.textPrimary,
            focusedContainerColor = WordsFairyTheme.colors.itemBackground,
            unfocusedContainerColor = WordsFairyTheme.colors.itemBackground,
            focusedIndicatorColor = Color.Transparent, // 有焦点时的颜色，透明
            unfocusedIndicatorColor = Color.Transparent, // 无焦点时的颜色，绿色
        ),
        trailingIcon = {
            if (text.isNotEmpty()) {
                Icon(
                    imageVector = Icons.Default.Clear,
                    contentDescription = null,
                    tint = WordsFairyTheme.colors.icon,
                    modifier = Modifier.clickable { onDeleteClick() }
                )
            }
        },
        shape = RoundedCornerShape(16.dp),
        keyboardActions = KeyboardActions {
            editFocusManager.clearFocus()
            onSend.invoke()
        },
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Text,
            imeAction = ImeAction.Go
        )
    )
}

@ExperimentalFoundationApi
@ExperimentalComposeUiApi
@Composable
fun LoginEditView(
    text: String,
    placeholder: String,
    keyboardType: KeyboardType = KeyboardType.Text,
    onValueChange: (String) -> Unit = {},
) {
    TextInputFieldOne(
        text = text,
        placeholder = placeholder,
        keyboardType = keyboardType,
        fontSize = 20.sp,
        imeAction = ImeAction.Next,
        onValueChange = onValueChange
    )
}


@OptIn(ExperimentalFoundationApi::class)
@ExperimentalComposeUiApi
@Composable
fun TextInputFieldOne(
    modifier: Modifier = Modifier,
    text: String,
    background: Color = WordsFairyTheme.colors.immerseBackground,
    placeholder: String = "",
    keyboardType: KeyboardType = KeyboardType.Text,
    readOnly: Boolean = false,
    singleLine: Boolean = true,
    imeAction: ImeAction? = null,
    keyboardActions: KeyboardActions? = null,
    fontSize: TextUnit = 16.sp,
    isError: Boolean = false,
    textFontWeight: FontWeight = FontWeight.Normal,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    onValueChange: (String) -> Unit = {},
) {
    val focusManager = LocalFocusManager.current
    val bringIntoViewRequester = remember { BringIntoViewRequester() }
    val interactionSource = remember { MutableInteractionSource() }
    val interactionSourceState = interactionSource.collectIsFocusedAsState()
    val scope = rememberCoroutineScope()
    val ime = LocalWindowInsets.current.ime
//    var textFieldValue by remember { mutableStateOf(TextFieldValue(text = text)) }

    // Bring the composable into view (visible to user).
    LaunchedEffect(ime.isVisible, interactionSourceState.value) {
        if (ime.isVisible && interactionSourceState.value) {
            scope.launch {
                delay(300)
                bringIntoViewRequester.bringIntoView()
            }
        }
    }

    BasicTextField(
        value = text,
        singleLine = singleLine,
        textStyle = TextStyle(
            fontFamily = MaterialTheme.typography.bodyMedium.fontFamily,
            fontSize = fontSize,
            fontWeight = textFontWeight,
            color = WordsFairyTheme.colors.textPrimary,
        ),
        onValueChange = {
            onValueChange(it)
        },
        keyboardActions = keyboardActions ?: KeyboardActions(
            onDone = { focusManager.clearFocus() },
            onNext = { focusManager.moveFocus(FocusDirection.Down) },
            onSearch = { focusManager.clearFocus() }
        ),
        keyboardOptions = KeyboardOptions(
            keyboardType = keyboardType,
            imeAction = imeAction ?: if (singleLine) ImeAction.Done else ImeAction.Default
        ),
        interactionSource = interactionSource,
        visualTransformation = visualTransformation,
        modifier = modifier
            .bringIntoViewRequester(bringIntoViewRequester)
            .fillMaxWidth(),
        readOnly = readOnly,
        decorationBox = { innerTextField ->
            Box(
                Modifier
                    .clip(MaterialTheme.shapes.medium)
                    .background(if (isError) WordsFairyTheme.colors.error else background)
                    // .height(height)
                    .padding(horizontal = 12.dp),
                contentAlignment = if (singleLine) Alignment.CenterStart else Alignment.TopStart,
            ) {
                Box(
                    Modifier
                        .fillMaxWidth()
                        .padding(
                            top = if (singleLine) 0.dp else 12.dp,
                            bottom = if (singleLine) 2.dp else 12.dp
                        )
                ) {
                    innerTextField()

                    if (text.isEmpty()) {
                        Text(
                            text = placeholder,
                            color = WordsFairyTheme.colors.textSecondary,
                            fontSize = fontSize,
                            fontWeight = textFontWeight,
                            maxLines = if (singleLine) 1 else Int.MAX_VALUE,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }
        }
    )
}



@OptIn(ExperimentalFoundationApi::class)
@ExperimentalComposeUiApi
@Composable
fun TextFieldInputField(
    modifier: Modifier = Modifier,
    textField: TextFieldValue,
    background: Color = WordsFairyTheme.colors.immerseBackground,
    placeholder: String = "",
    keyboardType: KeyboardType = KeyboardType.Text,
    readOnly: Boolean = false,
    singleLine: Boolean = true,
    maxLines: Int = Int.MAX_VALUE,
    imeAction: ImeAction? = null,
    keyboardActions: KeyboardActions? = null,
    fontSize: TextUnit = 16.sp,
    isError: Boolean = false,
    isAutoFocused: Boolean = false,
    textFontWeight: FontWeight = FontWeight.Normal,
    onValueChange: (TextFieldValue) -> Unit = {},
) {
    val focusManager = LocalFocusManager.current
    val bringIntoViewRequester = remember { BringIntoViewRequester() }
    val interactionSource = remember { MutableInteractionSource() }
    val interactionSourceState = interactionSource.collectIsFocusedAsState()
    val scope = rememberCoroutineScope()
    val ime = LocalWindowInsets.current.ime

    // Bring the composable into view (visible to user).
    LaunchedEffect(ime.isVisible, interactionSourceState.value) {
        if (ime.isVisible && interactionSourceState.value) {
            scope.launch {
                delay(300)
                bringIntoViewRequester.bringIntoView()
            }
        }
    }

    val focusRequester = remember { FocusRequester() }
    val keyboardController = LocalSoftwareKeyboardController.current
    if (isAutoFocused) {
        LaunchedEffect(Unit) {
            delay(300)
            focusRequester.requestFocus()
        }
    }

    BasicTextField(
        modifier = modifier
            .bringIntoViewRequester(bringIntoViewRequester)
            .focusRequester(focusRequester)
            .onFocusChanged {
                if (it.isFocused) {
                    if (isAutoFocused) {
                        keyboardController?.show()
                    }
                }
            }
            .fillMaxWidth(),
        value = textField,
        singleLine = singleLine,
        maxLines = maxLines,
        textStyle = TextStyle(
            fontFamily = MaterialTheme.typography.bodyMedium.fontFamily,
            fontSize = fontSize,
            fontWeight = textFontWeight,
            color = WordsFairyTheme.colors.textPrimary,
        ),
        onValueChange = {
            onValueChange(it)
        },
        keyboardActions = keyboardActions ?: KeyboardActions(
            onDone = { focusManager.clearFocus() },
            onNext = { focusManager.moveFocus(FocusDirection.Down) },
            onSearch = { focusManager.clearFocus() }
        ),
        keyboardOptions = KeyboardOptions(
            keyboardType = keyboardType,
            imeAction = imeAction ?: if (singleLine) ImeAction.Done else ImeAction.Default
        ),
        //导致回车换行，焦点消失，暂时注释，以后解决
       // interactionSource = interactionSource,
        readOnly = readOnly,
        decorationBox = { innerTextField ->
            Box(
                Modifier
                    .clip(MaterialTheme.shapes.medium)
                    .background(if (isError) WordsFairyTheme.colors.error else background)
                    // .height(height)
                    .padding(horizontal = 12.dp),
                contentAlignment = if (singleLine) Alignment.CenterStart else Alignment.TopStart,
            ) {
                Box(
                    Modifier
                        .fillMaxWidth()
                        .padding(
                            top = if (singleLine) 2.dp else 12.dp,
                            bottom = if (singleLine) 2.dp else 12.dp
                        )
                ) {
                    innerTextField()
                    if (textField.text.isEmpty()) {
                        Text(
                            text = placeholder,
                            color = WordsFairyTheme.colors.textSecondary,
                            fontSize = fontSize,
                            fontWeight = textFontWeight,
                            maxLines = if (singleLine) 1 else Int.MAX_VALUE,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }
        }
    )
}


@ExperimentalFoundationApi
@ExperimentalComposeUiApi
@Composable
fun PasswordEditView(
    text: String,
    placeholder: String,
    onValueChange: (String) -> Unit = {},
) {
    PasswordInputFieldOne(
        text = text,
        placeholder = placeholder,
        height = 59.dp,
        placeholderSize = 20.sp,
        imeAction = ImeAction.Go,
        onValueChange = onValueChange
    )
}

private val ELEMENT_HEIGHT = 49.dp


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun PasswordInputFieldOne(
    modifier: Modifier = Modifier,
    text: String,
    placeholder: String = "●●●●●●",
    readOnly: Boolean = false,
    fontSize: TextUnit = 16.sp,
    placeholderSize: TextUnit = 16.sp,
    imeAction: ImeAction = ImeAction.Done,
    keyboardActions: KeyboardActions? = null,
    height: Dp = ELEMENT_HEIGHT,
    isError: Boolean = false,
    onValueChange: (String) -> Unit = {},
) {
    val focusManager = LocalFocusManager.current
    var passwordVisibility by remember { mutableStateOf(false) }
    val bringIntoViewRequester = remember { BringIntoViewRequester() }
    val interactionSource = remember { MutableInteractionSource() }
    val interactionSourceState = interactionSource.collectIsFocusedAsState()
    val scope = rememberCoroutineScope()
    val ime = LocalWindowInsets.current.ime

    var textFieldValue by remember { mutableStateOf(TextFieldValue(text = text)) }
    // Bring the composable into view (visible to user).
    LaunchedEffect(ime.isVisible, interactionSourceState.value) {
        if (ime.isVisible && interactionSourceState.value) {
            scope.launch {
                delay(300)
                bringIntoViewRequester.bringIntoView()
            }
        }
    }

    BasicTextField(
        value = textFieldValue,
        singleLine = true,
        visualTransformation =
        if (passwordVisibility) VisualTransformation.None
        else PasswordVisualTransformation(mask = '●'),
        onValueChange = {
            textFieldValue = it

            onValueChange(it.text)
        },
        keyboardActions = keyboardActions ?: KeyboardActions(
            onDone = { focusManager.clearFocus() },
            onNext = { focusManager.moveFocus(FocusDirection.Down) }
        ),
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Password,
            imeAction = imeAction
        ),
        interactionSource = interactionSource,
        modifier = modifier
            .bringIntoViewRequester(bringIntoViewRequester)
            .fillMaxWidth(),
        readOnly = readOnly,
        textStyle = TextStyle(
            fontFamily = MaterialTheme.typography.bodyLarge.fontFamily,
            fontSize = fontSize,
            color = WordsFairyTheme.colors.textSecondary,
            textAlign = TextAlign.Center,
            letterSpacing = 2.sp
        ),
        decorationBox = { innerTextField ->
            Row(
                Modifier
                    .clip(MaterialTheme.shapes.medium)
                    .background(if (isError) WordsFairyTheme.colors.error else WordsFairyTheme.colors.immerseBackground)
                    .height(height),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Box(
                    Modifier
                        .weight(1f)
                        .padding(start = 53.dp, bottom = 2.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    innerTextField()

                    if (textFieldValue.text.isEmpty()) {
                        Text(
                            modifier = Modifier.padding(bottom = 2.dp),
                            text = placeholder,
                            color = WordsFairyTheme.colors.icon,
                            fontSize = placeholderSize,
                        )
                    }
                }

                Spacer(Modifier.width(6.dp))

                IconButton(
                    modifier = Modifier.align(Alignment.CenterVertically),
                    onClick = {
                        passwordVisibility = !passwordVisibility
                    }
                ) {
                    AnimatedVisibility(
                        visible = passwordVisibility,
                        enter = fadeIn(),
                        exit = fadeOut()
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.app_ic_edit_show),
                            "Show Password", tint = WordsFairyTheme.colors.icon
                        )
                    }

                    AnimatedVisibility(
                        visible = !passwordVisibility,
                        enter = fadeIn(),
                        exit = fadeOut()
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.app_ic_edit_hide),
                            "Hide Password", tint = WordsFairyTheme.colors.textPrimary
                        )
                    }
                }
            }
        }
    )
}


