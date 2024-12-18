package org.wspcgir.strong_giraffe.views

import android.util.Log
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.KeyboardType

@Composable
fun IntField(
    label: String,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    start: Int = 0,
    onChange: (Int?) -> Unit,
) {
    ValueField(
        label = label,
        enabled = enabled,
        modifier = modifier,
        start = start,
        fromString = String::toIntOrNull,
        onChange = onChange
    )
}

@Composable
fun FloatField(
    label: String,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    start: Float = 0f,
    onChange: (Float?) -> Unit
) {
    ValueField(
        label = label,
        enabled = enabled,
        modifier = modifier,
        start = start,
        fromString = String::toFloatOrNull,
        onChange = onChange
    )
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun <T> ValueField(
    label: String,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    singleLine: Boolean = true,
    start: T,
    fromString: (String) -> T?,
    onChange: (T?) -> Unit = { },
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    var text by remember(key1 = start) { mutableStateOf(start.toString()) }
    var valid by remember { mutableStateOf(true) }

    val validStateColors = TextFieldDefaults
        .textFieldColors()
    val errorStateColors = TextFieldDefaults.textFieldColors(
        containerColor = Color.Red.copy(alpha = 0.1f),
        errorTrailingIconColor = Color.Red,
        textColor = Color.Red
    )

    TextField(
        value = text,
        modifier = modifier,
        enabled = enabled,
        label = { Text(label) },
        keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
        keyboardActions = KeyboardActions(onDone = { keyboardController?.hide() }),
        colors = if (valid) { validStateColors } else { errorStateColors },
        trailingIcon = { if (!valid) { Icons.Default.Warning } },
        isError = !valid,
        singleLine = singleLine,
        onValueChange = { it ->
            text = it
            Log.d("TEXT_FIELD", "The value of it is '$it'")
            val value = fromString(it)
            Log.d("TEXT_FIELD", "The result of fromString is $value")
            valid = value != null
            onChange(value)
        }
    )
}