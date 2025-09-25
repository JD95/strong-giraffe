package org.wspcgir.strong_giraffe.destinations

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import org.wspcgir.strong_giraffe.views.ModalDrawerScaffold

@Composable
fun BottomBar(
    backupData: () -> Unit,
    restoreFromBackup: () -> Unit
) {
    Button(onClick = backupData) {
        Text("Backup")
    }
    Button(onClick = restoreFromBackup) {
        Text("Restore")
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomePage(
    gotoLocationsList: () -> Unit,
    gotoMuscleList: () -> Unit,
    gotoExerciseList: () -> Unit,
    gotoSetList: () -> Unit,
    createBackup: () -> Unit,
    restoreFromBackup: () -> Unit
) {
    ModalDrawerScaffold(
        title = "Strong Giraffe",
        drawerContent = { BottomBar(createBackup, restoreFromBackup) },
        actionButton = { }
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.3f),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceEvenly
            ) {
                val space = 10.dp
                Button(onClick = gotoLocationsList) {
                    Text(text = "Locations")
                }
                Button(onClick = gotoMuscleList) {
                    Text(text = "Muscles")
                }
                Button(onClick = gotoExerciseList) {
                    Text(text = "Exercises")
                }
                Button(onClick = gotoSetList) {
                    Text(text = "Sets")
                }
            }
        }
    }
}

@Preview
@Composable
fun HomePagePreview() {
    HomePage({}, {}, {}, {}, {}, {})
}
