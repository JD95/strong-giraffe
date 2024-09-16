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
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.ViewModel
import com.example.stronggiraffe.FIELD_NAME_FONT_SIZE
import com.example.stronggiraffe.views.LargeDropDownFromList
import com.example.stronggiraffe.model.Location
import com.example.stronggiraffe.model.ids.EquipmentId
import com.example.stronggiraffe.model.ids.LocationId
import com.ramcosta.composedestinations.annotation.Destination


abstract class EditEquipmentPageViewModel : ViewModel() {
    abstract val startingName: String
    abstract val locations: List<Location>
    abstract fun submit(value: String): Unit
}

data class EditEquipmentPageNavArgs(
    val id: EquipmentId
)

@OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class)
@Composable
@Destination(navArgsDelegate = EditEquipmentPageNavArgs::class)
fun EditEquipmentPage(view: EditEquipmentPageViewModel) {
    val keyboardController = LocalSoftwareKeyboardController.current
    var name by remember { mutableStateOf(view.startingName) }
    var selectedLocation by remember { mutableStateOf(view.locations[0]) }
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
            Text("Location", fontSize = FIELD_NAME_FONT_SIZE)
            LargeDropDownFromList(
                modifier = Modifier.fillMaxWidth(0.8f),
                items = view.locations,
                label = selectedLocation.name,
                itemToString = { it.name },
                onItemSelected = { selectedLocation = it }
            )
        }
    }
}

@Preview
@Composable
fun EditEquipmentPagePreview() {
    EditEquipmentPage(object: EditEquipmentPageViewModel() {
        override val startingName: String
            get() = "New Equipment"
        override val locations: List<Location>
            get() = listOf(Location(LocationId("a"), "24 Hour"))

        override fun submit(value: String) {
        }
    })
}