package org.wspcgir.strong_giraffe.destinations.edit_variation

import android.os.Parcelable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable
import org.wspcgir.strong_giraffe.model.variation.ExerciseVariation
import org.wspcgir.strong_giraffe.model.ids.ExerciseId
import org.wspcgir.strong_giraffe.model.ids.ExerciseVariationId
import org.wspcgir.strong_giraffe.model.ids.LocationId
import org.wspcgir.strong_giraffe.model.variation.VariationContent
import org.wspcgir.strong_giraffe.repository.AppRepository
import org.wspcgir.strong_giraffe.ui.theme.StrongGiraffeTheme
import org.wspcgir.strong_giraffe.views.ModalDrawerScaffold
import org.wspcgir.strong_giraffe.views.SelectionField
import org.wspcgir.strong_giraffe.views.TextField

class EditVariationPageViewModel : ViewModel() {
    private val _data: MutableState<Data> = mutableStateOf(Data.Empty)

    val data: State<Data>
        get() = _data

    fun init(
        repo: AppRepository,
        navController: NavController,
        id: ExerciseVariationId,
    ) {
        when (data.value) {
            is Data.Empty -> {
                viewModelScope.launch {
                    val variation = repo.getVariationForId(id)
                    _data.value = Data.Loaded(
                        navController = navController,
                        _variation = mutableStateOf(variation),
                        repo = repo,
                        scope = viewModelScope,
                    )
                }
            }

            else -> {}
        }
    }

    sealed interface Data {
        data class Loaded(
            private val scope: CoroutineScope,
            private val repo: AppRepository,
            private val navController: NavController,
            private val _variation: MutableState<VariationContent>
        ) : Data {
            val variation: State<VariationContent>
                get() = _variation

            fun updateVariationName(name: String) {
                _variation.value = variation.value.copy(name = name)
                submit()
            }

            fun updateVariationLocation(id: LocationId, name: String) {
                _variation.value = variation.value.copy(
                    location = id,
                    locationName = name
                )
                submit()
            }

            fun gotoSelectLocation() {
                navController.navigate(SelectLocation)
            }

            fun submit() {
                scope.launch {
                    repo.updateVariation(
                        id = _variation.value.id,
                        name = _variation.value.name,
                        location = _variation.value.location
                    )
                }
            }

            fun goBack() {
                navController.popBackStack()
            }
        }

        data object Empty : Data
    }
}

@Serializable
@Parcelize
data class EditVariation(val id: ExerciseVariationId): Parcelable

@Composable
fun EditVariationPage(
    viewModel: EditVariationPageViewModel
){
    when(val data = viewModel.data.value) {
        is EditVariationPageViewModel.Data.Loaded -> {
            Page(
                state = data.variation,
                onEditName = { data.updateVariationName(it) },
                onSelectLocation = { data.gotoSelectLocation() },
                onSubmit = {
                    data.submit()
                    data.goBack()
                },
            )
        }
        else -> { CircularProgressIndicator() }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun Page(
    state: State<VariationContent>,
    onEditName: (String) -> Unit,
    onSelectLocation: () -> Unit,
    onSubmit: () -> Unit,
) {
    ModalDrawerScaffold(
        title = "Edit Variation",
        actionButton = {
            FloatingActionButton(
                onClick = onSubmit
            ) {
                Icon(Icons.Filled.Check, contentDescription = "select button")
            }
        },
        drawerContent = {}
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxHeight()
                .fillMaxWidth(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(20.dp)) {
                TextField(
                    label = "Name",
                    start = state.value.name,
                    onChange = { onEditName(it) },
                    modifier = Modifier.fillMaxWidth(0.8f)
                )
                SelectionField(
                    label = "Location",
                    text = state.value.locationName ?: "No Location",
                    onClick = onSelectLocation,
                    modifier = Modifier.fillMaxWidth(0.8f)
                )
            }
        }
    }
}

@Composable
@Preview
private fun Preview() {
    StrongGiraffeTheme {
        Page(
            state = remember {
                mutableStateOf(
                    VariationContent(
                        id = ExerciseVariationId("variationA"),
                        name = "Bleh",
                        location = LocationId("locationA"),
                        locationName = "home",
                    )
                )
            },
            onEditName = { },
            onSelectLocation = { },
            onSubmit = { },
        )
    }
}