package org.wspcgir.strong_giraffe.destinations

import android.os.Parcelable
import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import org.wspcgir.strong_giraffe.views.LargeDropDownFromList
import org.wspcgir.strong_giraffe.model.Muscle
import org.wspcgir.strong_giraffe.model.ids.ExerciseId
import org.wspcgir.strong_giraffe.model.ids.MuscleId
import org.wspcgir.strong_giraffe.views.FIELD_NAME_FONT_SIZE
import org.wspcgir.strong_giraffe.views.RequiredDataRedirect
import kotlinx.coroutines.launch
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable
import org.wspcgir.strong_giraffe.model.Exercise
import org.wspcgir.strong_giraffe.model.ExerciseVariation
import org.wspcgir.strong_giraffe.model.Location
import org.wspcgir.strong_giraffe.model.ids.ExerciseVariationId
import org.wspcgir.strong_giraffe.repository.AppRepository
import org.wspcgir.strong_giraffe.views.ModalDrawerScaffold

@Serializable
@Parcelize
data class EditExercise(
    val id: ExerciseId,
) : Parcelable

abstract class EditExercisePageViewModel() : ViewModel() {
    abstract val exercise: State<Exercise?>
    abstract val selectedMuscle: State<Muscle?>
    abstract val muscles: State<List<Muscle>>
    abstract val variations: State<List<State<ExerciseVariation>>>
    abstract val locations: State<List<Location>>

    abstract fun submit(name: String, muscle: MuscleId)
    abstract fun redirectToCreateMuscle()
    abstract fun delete()
    abstract fun updateVariationLocation(
        variation: ExerciseVariation,
        location: Location?
    )

    abstract fun updateVariationName(
        variation: ExerciseVariation,
        name: String
    )

    abstract fun addNewVariation()
    abstract fun changeName(name: String)
    abstract fun selectMuscle(muscle: Muscle)
}

class EditExercisePageViewModelImpl(
    private val id: ExerciseId,
    private val repo: AppRepository,
    private val nav: NavController
) : EditExercisePageViewModel() {

    private var variationsMap:
            Map<ExerciseVariationId, MutableState<ExerciseVariation>> = emptyMap()

    private val exerciseMut: MutableState<Exercise?> = mutableStateOf(null)
    private val selectedMuscleMut: MutableState<Muscle?> = mutableStateOf(null)
    private val variationsMut: MutableState<List<State<ExerciseVariation>>> =
        mutableStateOf(emptyList())
    private val musclesMut: MutableState<List<Muscle>> = mutableStateOf(emptyList())
    private val locationsMut: MutableState<List<Location>> = mutableStateOf(emptyList())

    init {
        viewModelScope.launch {
            exerciseMut.value = repo.getExerciseFromId(id)
            val vars = repo.getVariationsForExercise(id)
            Log.d("EditExercisePageViewModelImpl.init", "$vars")
            variationsMap = vars
                .foldRight(emptyMap()) { x, xs -> xs.plus(x.id to mutableStateOf(x)) }
            rebuildVariationsListFromMap()
            musclesMut.value = repo.getMuscles()
            selectedMuscleMut.value = musclesMut.value
                .first { it.id == exerciseMut.value?.muscle }
            locationsMut.value = repo.getLocations()
            Log.i("DEBUG", "EditExercisePageViewModel initialized")
            Log.i("DEBUG", "The exercise is ${exerciseMut.value}")
            Log.i("DEBUG", "The muscle is ${selectedMuscleMut.value}")
        }
    }

    override val exercise: State<Exercise?>
        get() = exerciseMut

    override val selectedMuscle: State<Muscle?>
        get() = selectedMuscleMut

    override val muscles: State<List<Muscle>>
        get() = musclesMut

    override val variations: State<List<State<ExerciseVariation>>>
        get() = variationsMut

    override val locations: State<List<Location>>
        get() = locationsMut

    override fun submit(name: String, muscle: MuscleId) {
        viewModelScope.launch {
            repo.updateExercise(id, name, muscle)
        }
        nav.popBackStack()
    }

    override fun redirectToCreateMuscle() {
        viewModelScope.launch {
            val new = repo.newMuscle()
            nav.navigate(EditMuscle(new.id, new.name))
        }
    }

    override fun delete() {
        viewModelScope.launch {
            repo.deleteExercise(id)
        }
        nav.popBackStack()
    }

    override fun updateVariationLocation(variation: ExerciseVariation, location: Location?) {
        viewModelScope.launch {
            variationsMap[variation.id]?.let {
                it.value = it.value.copy(location = location?.id)
            }
            repo.updateVariation(variation.id, variation.name, location?.id)
        }
    }

    override fun updateVariationName(variation: ExerciseVariation, name: String) {
        viewModelScope.launch {
            variationsMap[variation.id]?.let {
                it.value = it.value.copy(name = name)
            }
            repo.updateVariation(variation.id, name, variation.location)
        }
    }

    override fun addNewVariation() {
        viewModelScope.launch {
            val variation = repo.newExerciseVariation(id)
            variationsMap = variationsMap.plus(variation.id to mutableStateOf(variation))
            rebuildVariationsListFromMap()
        }
    }

    override fun changeName(name: String) {
        exerciseMut.value = exerciseMut.value?.copy(name = name)
    }

    override fun selectMuscle(muscle: Muscle) {
        exerciseMut.value = exerciseMut.value?.copy(muscle = muscle.id)
        selectedMuscleMut.value = muscle
    }

    private fun rebuildVariationsListFromMap() {
        variationsMut.value = variationsMap.values.toList()
    }
}


@Composable
fun EditExercisePage(view: EditExercisePageViewModel) {
    if (view.muscles.value.isEmpty()) {
        RequiredDataRedirect(missing = "Muscle") {
            view.redirectToCreateMuscle()
        }
    } else {
        key(view.exercise.value, view.selectedMuscle.value) {
            if (view.exercise.value != null && view.selectedMuscle.value != null) {
                Page(view)
            } else {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    CircularProgressIndicator(modifier = Modifier.fillMaxSize(0.6f))
                }
            }
        }
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class)
private fun Page(view: EditExercisePageViewModel) {
    val keyboardController = LocalSoftwareKeyboardController.current
    ModalDrawerScaffold(
        title = "Edit Exercise",
        actionButton = {
            Row(
                horizontalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                FloatingActionButton(
                    onClick = { view.addNewVariation() }
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Add Variation")
                }

                FloatingActionButton(
                    onClick = {
                        view.submit(
                            view.exercise.value!!.name,
                            view.exercise.value!!.muscle
                        )
                    }
                ) {
                    Icon(Icons.Default.Done, contentDescription = "Save Location")
                }
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
            verticalArrangement = Arrangement.spacedBy(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Column {
                Text("Name", fontSize = FIELD_NAME_FONT_SIZE)
                var exerciseName by remember { mutableStateOf(view.exercise.value!!.name) }
                TextField(
                    modifier = Modifier
                        .fillMaxWidth(0.8f)
                        .onFocusChanged { if (!it.isFocused) { view.changeName(exerciseName) } },
                    value = exerciseName,
                    onValueChange = { exerciseName = it },
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                    keyboardActions = KeyboardActions(
                        onDone = {
                            view.changeName(exerciseName)
                            keyboardController?.hide()
                        }
                    )
                )
            }
            Column {
                Text("Muscle", fontSize = FIELD_NAME_FONT_SIZE)
                LargeDropDownFromList(
                    modifier = Modifier.fillMaxWidth(0.8f),
                    items = view.muscles.value,
                    label = view.selectedMuscle.value!!.name,
                    itemToString = { it.name },
                    onItemSelected = { view.selectMuscle(it) },
                    selectedIndex = view.muscles.value.indexOf(view.selectedMuscle.value)
                )
            }
            Column {
                Text("Variations", fontSize = FIELD_NAME_FONT_SIZE)
                LazyColumn(
                    modifier = Modifier.fillMaxWidth(0.8f),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(view.variations.value) { variation ->
                        VariationCard(
                            value = variation.value,
                            locations = view.locations.value,
                            onChangeLocation = {
                                view.updateVariationLocation(variation.value, it)
                                Log.d("EditExercisePage", "Location Updated")
                            },
                            onChangeName = { view.updateVariationName(variation.value, it) }
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VariationCard(
    value: ExerciseVariation,
    locations: List<Location>,
    onChangeLocation: (Location?) -> Unit,
    onChangeName: (String) -> Unit
) {
    val locationsAndNull = listOf(null).plus(locations)
    val keyboardController = LocalSoftwareKeyboardController.current
    var name by remember { mutableStateOf(value.name) }
    key(value.name, value.location) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(10.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                TextField(name, onValueChange = { name = it },
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                    keyboardActions = KeyboardActions(
                        onDone = {
                            keyboardController?.hide()
                            onChangeName(name)
                        }
                    ),
                    modifier = Modifier.onFocusChanged {
                        if (!it.isFocused) {
                            onChangeName(name)
                        }
                    }
                )
                LargeDropDownFromList(
                    items = locationsAndNull,
                    label = "Location",
                    selectedIndex = locationsAndNull.map { it?.id }.indexOf(value.location),
                    itemToString = { it?.name ?: "None" },
                    onItemSelected = onChangeLocation,
                    modifier = Modifier
                )
            }
        }
    }
}

// @Composable
// @Preview
// private fun Preview() {
//     val bicep = Muscle(MuscleId("bicep"), "bicep")
//     val exercise = ExerciseId("exercise")
//     StrongGiraffeTheme {
//         Page(object : EditExercisePageViewModel(exercise) {
//             override val startingName: String
//                 get() = "Bicep Curl"
//             override val startingMuscle: Muscle
//                 get() = bicep
//             override val muscles: List<Muscle>
//                 get() = listOf(bicep)
//             override val variations: State<List<ExerciseVariation>>
//                 get() = mutableStateOf(
//                     "Really Fucking Long Name,b,c,d,e,f,g".split(',').map {
//                         ExerciseVariation(
//                             id = ExerciseVariationId("variation$it"),
//                             name = it,
//                             exercise = ExerciseId("a"),
//                             location = null
//                         )
//                     }
//                 )
//             override val locations: State<List<Location>>
//                 get() = mutableStateOf(listOf(Location(LocationId("place"), "place")))
//
//             override fun submit(name: String, muscle: Muscle) {
//                 TODO("Not yet implemented")
//             }
//
//             override fun redirectToCreateMuscle() {
//                 TODO("Not yet implemented")
//             }
//
//             override fun delete() {
//                 TODO("Not yet implemented")
//             }
//
//             override fun updateVariationLocation(
//                 variation: ExerciseVariation,
//                 location: Location?
//             ) {
//                 TODO("Not yet implemented")
//             }
//
//             override fun updateVariationName(variation: ExerciseVariation, name: String) {
//                 TODO("Not yet implemented")
//             }
//
//             override fun addNewVariation() {
//                 TODO("Not yet implemented")
//             }
//         })
//     }
// }