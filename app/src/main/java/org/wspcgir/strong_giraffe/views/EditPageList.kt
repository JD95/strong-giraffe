package org.wspcgir.strong_giraffe.views

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
    sortBy: ((T,T) -> Int)? = null,
    rowRender: @Composable (((T) -> Unit), (@Composable (T) -> Unit), T) -> Unit =
        { onClick, inner, item ->
            Button(onClick = { onClick(item) }) {
                inner(item)
            }
        },
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
        Column(modifier = Modifier.padding(innerPadding)) {
            Column(
                modifier = Modifier.fillMaxWidth().fillMaxHeight(0.2f),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(title, fontSize = PAGE_TITLE_FONTSIZE)
            }
            Column(
                modifier = Modifier.fillMaxWidth().fillMaxHeight(0.8f),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                if (items.isNotEmpty()) {
                    val ordered = if (sortBy != null) {
                        items.sortedWith(sortBy)
                    } else items
                    LazyColumn {
                        this.items(ordered) { item ->
                            rowRender(gotoEditPage, itemRow, item)
                        }
                    }
                } else {
                    Text("There's nothing here yet")
                }
            }
        }
    }
}

@Preview
@Composable
private fun Preview(){
    EditPageList(
        title = "Fruits",
        items = (0..100).toList(),
        gotoNewPage = { },
        gotoEditPage = { }
    ) {
        Text(it.toString())
    }
}