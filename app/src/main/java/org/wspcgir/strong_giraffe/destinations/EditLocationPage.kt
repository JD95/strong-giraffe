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
import org.wspcgir.strong_giraffe.model.ids.LocationId
import org.wspcgir.strong_giraffe.views.FIELD_NAME_FONT_SIZE
import com.ramcosta.composedestinations.annotation.Destination

abstract class EditLocationPageViewModel : ViewModel() {
    abstract val startingName: String
    abstract val submit: (String) -> Unit
    abstract fun delete()
}

data class EditLocationPageNavArgs(
    val startingName: String,
    val id: LocationId,
)

@OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class)
@Composable
@Destination(navArgsDelegate = EditLocationPageNavArgs::class)
fun EditLocationPage(view: EditLocationPageViewModel) {
    val keyboardController = LocalSoftwareKeyboardController.current
    var name by remember { mutableStateOf(view.startingName) }
    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { view.submit(name) }
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
            Spacer(modifier = Modifier.fillMaxHeight(0.1f))
            Button(onClick = view::delete) {
                Text("Delete")
                Spacer(modifier = Modifier.fillMaxWidth(0.03f))
                Icon(Icons.Default.Delete, contentDescription = "delete location")
            }
        }
    }
}