package org.wspcgir.strong_giraffe.views

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun <T> EditPageList(
    title: String,
    items: List<T>,
    gotoNewPage: () -> Unit,
    gotoEditPage: (T) -> Unit,
    itemRow: @Composable (T) -> Unit,
) {
    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { gotoNewPage() }
            ) {
                Icon(Icons.Default.Add, contentDescription = "Create New")
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(title, fontSize = PAGE_TITLE_FONTSIZE)
            Spacer(modifier = Modifier.fillMaxHeight(0.2f))
            if (items.isNotEmpty()) {
                items.forEach { item ->
                    Button(onClick = { gotoEditPage(item) }) {
                        itemRow(item)
                    }
                }
            } else {
                Text("There's nothing here yet")
            }
        }
    }
}

@Preview
@Composable
private fun Preview(){
    EditPageList(
        title = "Fruits",
        items = listOf("apple"), //, "banana", "coconut", "durian"),
        gotoNewPage = { },
        gotoEditPage = { }
    ) {
        Text(it)
    }
}