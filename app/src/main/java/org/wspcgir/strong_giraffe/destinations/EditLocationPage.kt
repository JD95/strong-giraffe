package org.wspcgir.strong_giraffe.destinations

import android.os.Bundle
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
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.lifecycle.ViewModel
import androidx.navigation.NavType
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import org.wspcgir.strong_giraffe.model.ids.LocationId
import org.wspcgir.strong_giraffe.views.FIELD_NAME_FONT_SIZE
import org.wspcgir.strong_giraffe.views.ModalDrawerScaffold

abstract class EditLocationPageViewModel : ViewModel() {
    abstract val startingName: String
    abstract val submit: (String) -> Unit
    abstract fun delete()
}

@Serializable
@Parcelize
data class EditLocation(
    val startingName: String,
    val id: LocationId,
) : Parcelable

val EditLocationType = object : NavType<EditLocation>(
    isNullableAllowed = false
) {
    override fun get(bundle: Bundle, key: String): EditLocation? {
        return bundle.getParcelable(key, EditLocation::class.java)
    }

    override fun parseValue(value: String): EditLocation {
        return Json.decodeFromString<EditLocation>(value)
    }

    override fun put(bundle: Bundle, key: String, value: EditLocation) {
        bundle.putParcelable(key, value)
    }

}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class)
@Composable
fun EditLocationPage(view: EditLocationPageViewModel) {
    val keyboardController = LocalSoftwareKeyboardController.current
    var name by remember { mutableStateOf(view.startingName) }
    ModalDrawerScaffold(
        title = "Edit Location",
        actionButton = {
            FloatingActionButton(
                onClick = { view.submit(name) }
            ) {
                Icon(Icons.Default.Done, contentDescription = "Save Location")
            }
        },
        drawerContent = {
            Button(onClick = view::delete) {
                Text("Delete")
                Spacer(modifier = Modifier.fillMaxWidth(0.03f))
                Icon(Icons.Default.Delete, contentDescription = "delete location")
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