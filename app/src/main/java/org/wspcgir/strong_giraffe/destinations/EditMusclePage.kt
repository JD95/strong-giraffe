package org.wspcgir.strong_giraffe.destinations

import android.os.Parcelable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.lifecycle.ViewModel
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable
import org.wspcgir.strong_giraffe.model.ids.MuscleId
import org.wspcgir.strong_giraffe.views.FIELD_NAME_FONT_SIZE
import org.wspcgir.strong_giraffe.views.ModalDrawerScaffold

@Serializable
@Parcelize
data class EditMuscle(val muscleId: MuscleId, val startingName: String) : Parcelable

abstract class EditMusclePageViewModel : ViewModel() {
    abstract val startingName: String
    abstract fun submit(name: String)
    abstract fun delete()

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditMusclePage(view: EditMusclePageViewModel) {
    val keyboardController = LocalSoftwareKeyboardController.current
    var name by remember { mutableStateOf(view.startingName) }
    ModalDrawerScaffold(
        title = "Edit Muscle",
        actionButton = {
            FloatingActionButton(
                onClick = { view.submit(name) }
            ) {
                Icon(Icons.Default.Done, contentDescription = "Save Muscle")
            }
        },
        drawerContent = {
            Button(onClick = view::delete) {
                Text("Delete")
                Spacer(modifier = Modifier.fillMaxWidth(0.03f))
                Icon(Icons.Default.Delete, contentDescription = "delete muscle")
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
        }
    }
}