package org.wspcgir.strong_giraffe.views

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import org.wspcgir.strong_giraffe.ui.theme.StrongGiraffeTheme

@Composable
fun <T> LargeDropDownMenu(
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    notSetLabel: String? = null,
    items: List<T>,
    selectedIndex: Int = -1,
    onItemSelected: (index: Int, item: T) -> Unit,
    selectedItemToString: (T) -> String = { it.toString() },
    drawItem: @Composable (T, Boolean, Boolean, () -> Unit) -> Unit = { item, selected, itemEnabled, onClick ->
        LargeDropDownMenuItem(
            text = item.toString(),
            selected = selected,
            enabled = itemEnabled,
            onClick = onClick,
        )
    },
) {
    val expanded = remember { mutableStateOf(false) }

    Box(modifier = modifier.height(IntrinsicSize.Min)) {
        TextField(
            label = {},
            value = items.getOrNull(selectedIndex)?.let { selectedItemToString(it) } ?: "",
            enabled = enabled,
            modifier = Modifier.fillMaxWidth(),
            trailingIcon = {
                val icon = if (expanded.value) {
                    Icons.Filled.KeyboardArrowUp
                } else {
                    Icons.Filled.ArrowDropDown
                }
                Icon(icon, "")
            },
            onValueChange = { },
            readOnly = true,
        )

        // Transparent clickable surface on top of OutlinedTextField
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 8.dp)
                .clip(MaterialTheme.shapes.small)
                .clickable(enabled = enabled) { expanded.value = true },
            color = Color.Transparent
        ) {}
    }

    if (expanded.value) {
        Dialog(
            onDismissRequest = { expanded.value = false },
        ) {
            var searchString by remember { mutableStateOf("") }
            Column {

                Spacer(modifier = Modifier.weight(0.1f))

                Column(
                    modifier = Modifier.weight(0.9f)
                            .wrapContentHeight()
                        .padding(top = 10.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                    ) {
                        val listState = rememberLazyListState()
                        if (selectedIndex > -1) {
                            LaunchedEffect("ScrollToSelected") {
                                listState.scrollToItem(index = selectedIndex)
                            }
                        }

                        LazyColumn(
                            modifier = Modifier.fillMaxWidth(),
                            state = listState
                        ) {
                            if (notSetLabel != null) {
                                item {
                                    LargeDropDownMenuItem(
                                        text = notSetLabel,
                                        selected = false,
                                        enabled = false,
                                        onClick = { },
                                    )
                                }
                            }

                            val visibleItems =
                                if (searchString.isNotEmpty()) {
                                    items.filter { t ->
                                        selectedItemToString(t).contains(
                                            searchString
                                        )
                                    }
                                } else {
                                    items
                                }.withIndex().toList()

                            items(visibleItems) { indexed ->
                                val item = indexed.value
                                val index = indexed.index
                                val selectedItem = index == selectedIndex
                                val rowModifier =
                                    if (index.mod(2) == 0) {
                                        Modifier.background(color = Color.Black.copy(alpha = 0.05f))
                                    } else {
                                        Modifier
                                    }

                                Row(
                                    modifier = rowModifier,
                                ) {
                                    drawItem(
                                        item,
                                        selectedItem,
                                        true
                                    ) {
                                        onItemSelected(index, item)
                                        expanded.value = false
                                    }
                                }
                            }
                        }
                    }
                }

                val keyboardController = LocalSoftwareKeyboardController.current
                TextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight()
                        .weight(0.1f)
                        .padding(top = 20.dp),
                    value = searchString,
                    onValueChange = { str -> searchString = str },
                    trailingIcon = { Icon(Icons.Default.Search, contentDescription = "search") },
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                    keyboardActions = KeyboardActions(onDone = { keyboardController?.hide() })
                )
            }
        }
    }
}

@Composable
fun LargeDropDownMenuItem(
    text: String,
    selected: Boolean,
    enabled: Boolean,
    onClick: () -> Unit,
) {
    val contentColor = when {
        !enabled -> MaterialTheme.colorScheme.onSurface.copy(alpha = 0.0f)
        selected -> MaterialTheme.colorScheme.primary.copy(alpha = 1.0f)
        else -> MaterialTheme.colorScheme.onSurface.copy(alpha = 1.0f)
    }

    CompositionLocalProvider(LocalContentColor provides contentColor) {
        Box(modifier = Modifier
            .clickable(enabled) { onClick() }
            .fillMaxWidth()
            .padding(16.dp)) {
            Text(
                text = text,
                style = MaterialTheme.typography.labelSmall,
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
private fun Preview() {
    StrongGiraffeTheme {
        ModalDrawerScaffold(
            title = "test",
            drawerContent = { },
            actionButton = { },
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                LargeDropDownMenu(
                    modifier = Modifier,
                    enabled = true,
                    notSetLabel = null,
                    items = listOf(
                        "Apple",
                        "Apple",
                        "Banana",
                        "Rhino"
                    ),
                    selectedIndex = 0,
                    onItemSelected = { _, _ -> },
                    selectedItemToString = { x -> x },
                )
            }
        }
    }

}