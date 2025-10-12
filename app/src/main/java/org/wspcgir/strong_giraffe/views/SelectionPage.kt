package org.wspcgir.strong_giraffe.views

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.wspcgir.strong_giraffe.ui.theme.StrongGiraffeTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SelectionPage() {
    ModalDrawerScaffold(
        title = "Exercise",
        drawerContent = { },
        actionButton = { }
    ) {
        LazyColumn(
            modifier = Modifier
                .padding(10.dp)
                .fillMaxHeight()
                .fillMaxWidth(),
            verticalArrangement = Arrangement.SpaceEvenly,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            val max = 20
            for (i in 0..max) {
                item() {
                    Row (modifier = Modifier.fillMaxWidth(0.8f)) {
                        ExerciseRow()
                    }
                    if (i < max) {
                        Divider(
                            modifier = Modifier.fillMaxWidth(),
                            color = Color.Black.copy(alpha = 0.10f)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ExerciseRow() {
    Row (
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(5.dp)
    ) {
        Column(modifier = Modifier.weight(0.9f)) {
            Text("test", fontSize = 30.sp)
        }
        Column(modifier = Modifier.weight(0.1f)) {
            IconButton(
                onClick = {}
            ) {
                Icon(Icons.Filled.Build, contentDescription = "hello", modifier = Modifier.scale(1.0f))
            }
        }
    }
}

@Composable
@Preview()
private fun Preview() {
    StrongGiraffeTheme {
        SelectionPage()
    }
}