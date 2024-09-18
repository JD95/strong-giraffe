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
import com.example.stronggiraffe.model.ids.MuscleId
import com.example.stronggiraffe.views.FIELD_NAME_FONT_SIZE
import com.ramcosta.composedestinations.annotation.Destination

data class EditMusclePageNavArgs(val muscleId: MuscleId, val startingName: String)

abstract class EditMusclePageViewModel: ViewModel() {
    abstract val startingName: String
    abstract fun submit(name: String)

}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class)
@Composable
@Destination(navArgsDelegate = EditMusclePageNavArgs::class)
fun EditMusclePage(view: EditMusclePageViewModel) {
    val keyboardController = LocalSoftwareKeyboardController.current
    var name by remember { mutableStateOf(view.startingName) }
    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { view.submit(name) }
            ) {
                Icon(Icons.Default.Done, contentDescription = "Save Muscle")
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