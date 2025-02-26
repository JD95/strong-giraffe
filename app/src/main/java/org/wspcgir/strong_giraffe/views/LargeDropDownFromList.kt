package org.wspcgir.strong_giraffe.views

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun <T> LargeDropDownFromList(
    items: List<T>,
    enabled: Boolean = true,
    selectedIndex: Int,
    itemToString: (T) -> String,
    onItemSelected: (T) -> Unit,
    modifier: Modifier
) {
    LargeDropDownMenu(
        modifier = modifier,
        enabled = enabled,
        items = items,
        onItemSelected = { _, x -> onItemSelected(x) },
        selectedItemToString = itemToString,
        selectedIndex = selectedIndex,
        drawItem = { item, selected, isEnabled, onClick ->
            LargeDropDownMenuItem(
                text = itemToString(item),
                selected = selected,
                enabled = isEnabled,
                onClick = onClick
            )
        }
    )
}