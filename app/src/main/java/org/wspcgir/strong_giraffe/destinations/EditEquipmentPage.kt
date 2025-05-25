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
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.ViewModel
import org.wspcgir.strong_giraffe.views.LargeDropDownFromList
import org.wspcgir.strong_giraffe.model.Location
import org.wspcgir.strong_giraffe.model.ids.EquipmentId
import org.wspcgir.strong_giraffe.model.ids.LocationId
import org.wspcgir.strong_giraffe.views.FIELD_NAME_FONT_SIZE
import org.wspcgir.strong_giraffe.views.RequiredDataRedirect
import org.wspcgir.strong_giraffe.views.ModalDrawerScaffold


abstract class EditEquipmentPageViewModel : ViewModel() {
    abstract val startingName: String
    abstract val locations: List<Location>
    abstract val startingLocation: Location
    abstract fun submit(name: String, location: LocationId)
    abstract fun redirectToCreateLocation()
    abstract fun delete()
}

@Composable
fun EditEquipmentPage(view: EditEquipmentPageViewModel) {
    if (view.locations.isEmpty()) {
        RequiredDataRedirect(missing = "Location") {
           view.redirectToCreateLocation()
        }
    } else {
        Page(view)
    }
}

@Composable
fun Page(view: EditEquipmentPageViewModel) {
    val keyboardController = LocalSoftwareKeyboardController.current
    var name by remember { mutableStateOf(view.startingName) }
    var selectedLocation by remember { mutableStateOf(view.startingLocation) }
    ModalDrawerScaffold(
        title = "Edit Equipment",
        actionButton = {
            FloatingActionButton(
                onClick = { view.submit(name, selectedLocation.id) }
            ) {
                Icon(Icons.Default.Done, contentDescription = "Save Location")
            }
        },
        drawerContent = {
            Button(onClick = view::delete) {
                Text("Delete")
                Spacer(modifier = Modifier.fillMaxWidth(0.03f))
                Icon(Icons.Default.Delete, contentDescription = "delete equipment")
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
                selectedIndex = view.locations.indexOfFirst { it.id == selectedLocation.id },
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
            get() = listOf(
                Location(LocationId("a"), "24 Hour"),
                Location(LocationId("b"), "Planet Fitness"),
            )
        override val startingLocation: Location
            get() = Location(LocationId("b"), "24 Hour")

        override fun submit(name: String, location: LocationId) {
            TODO("Not yet implemented")
        }

        override fun redirectToCreateLocation() {

        }

        override fun delete() {

        }
    })
}