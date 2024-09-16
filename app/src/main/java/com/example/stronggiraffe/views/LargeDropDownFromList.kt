package com.example.stronggiraffe.views

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun <T> LargeDropDownFromList(
    items: List<T>,
    label: String,
    itemToString: (T) -> String,
    onItemSelected: (T) -> Unit,
    modifier: Modifier
) {
    LargeDropDownMenu(
        modifier = modifier,
        label = label,
        items = items,
        onItemSelected = { _, x -> onItemSelected(x) },
        selectedItemToString = itemToString,
        drawItem = { item, selected, enabled, onClick ->
            LargeDropDownMenuItem(
                text = itemToString(item),
                selected = selected,
                enabled = enabled,
                onClick = onClick
            )
        }
    )
}