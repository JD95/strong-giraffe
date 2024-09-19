package com.example.stronggiraffe.destinations

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.lifecycle.ViewModel
import com.example.stronggiraffe.views.LargeDropDownFromList
import com.example.stronggiraffe.model.Muscle
import com.example.stronggiraffe.model.ids.ExerciseId
import com.example.stronggiraffe.model.ids.MuscleId
import com.example.stronggiraffe.views.FIELD_NAME_FONT_SIZE
import com.example.stronggiraffe.views.RequiredDataRedirect
import com.ramcosta.composedestinations.annotation.Destination

data class EditExercisePageNavArgs(
    val id: ExerciseId,
    val startingName: String,
    val startingMuscle: MuscleId,
)

abstract class EditExercisePageViewModel() : ViewModel() {
    abstract val startingName: String
    abstract val startingMuscle: Muscle
    abstract val muscles: List<Muscle>
    abstract fun submit(name: String, muscle: Muscle)
    abstract fun redirectToCreateMuscle()
}

@Composable
@Destination(navArgsDelegate = EditExercisePageNavArgs::class)
fun EditExercisePage(view: EditExercisePageViewModel) {
    if (view.muscles.isEmpty()) {
        RequiredDataRedirect(missing = "Muscle") {
            view.redirectToCreateMuscle()
        }
    } else {
        Page(view)
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class)
private fun Page(view: EditExercisePageViewModel){
    val keyboardController = LocalSoftwareKeyboardController.current
    var name by remember { mutableStateOf(view.startingName) }
    var selectedMuscle by remember { mutableStateOf(view.startingMuscle) }
    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { view.submit(name, selectedMuscle) }
            ) {
                Icon(Icons.Default.Done, contentDescription = "Save Location")
            }
        }
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Name", fontSize = FIELD_NAME_FONT_SIZE)
            TextField(
                modifier = Modifier.fillMaxWidth(0.8f),
                value = name,
                onValueChange = { name = it; },
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(onDone = { keyboardController?.hide() })
            )
            Text("Muscle", fontSize = FIELD_NAME_FONT_SIZE)
            LargeDropDownFromList(
                modifier = Modifier.fillMaxWidth(0.8f),
                items = view.muscles,
                label = selectedMuscle.name,
                itemToString = { it.name },
                onItemSelected = { selectedMuscle = it },
                selectedIndex = view.muscles.indexOf(selectedMuscle)
            )
        }
    }
}
