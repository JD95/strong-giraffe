package org.wspcgir.strong_giraffe.destinations

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.lifecycle.ViewModel
import org.wspcgir.strong_giraffe.views.LargeDropDownFromList
import org.wspcgir.strong_giraffe.model.Muscle
import org.wspcgir.strong_giraffe.model.ids.ExerciseId
import org.wspcgir.strong_giraffe.model.ids.MuscleId
import org.wspcgir.strong_giraffe.views.FIELD_NAME_FONT_SIZE
import org.wspcgir.strong_giraffe.views.RequiredDataRedirect
import com.ramcosta.composedestinations.annotation.Destination
import org.wspcgir.strong_giraffe.views.ModalDrawerScaffold

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
    abstract fun delete()
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
    ModalDrawerScaffold(
        title = "Edit Exercise",
        actionButton = {
            FloatingActionButton(
                onClick = { view.submit(name, selectedMuscle) }
            ) {
                Icon(Icons.Default.Done, contentDescription = "Save Location")
            }
        },
        drawerContent = {
            Button(onClick = view::delete) {
                Text("Delete")
                Spacer(modifier = Modifier.fillMaxWidth(0.03f))
                Icon(Icons.Default.Delete, contentDescription = "delete exercise")
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
