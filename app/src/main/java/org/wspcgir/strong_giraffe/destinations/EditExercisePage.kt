package org.wspcgir.strong_giraffe.destinations

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
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.SoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import org.wspcgir.strong_giraffe.views.LargeDropDownFromList
import org.wspcgir.strong_giraffe.model.Muscle
import org.wspcgir.strong_giraffe.model.ids.ExerciseId
import org.wspcgir.strong_giraffe.views.FIELD_NAME_FONT_SIZE
import org.wspcgir.strong_giraffe.views.RequiredDataRedirect
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import kotlinx.coroutines.launch
import org.wspcgir.strong_giraffe.model.Exercise
import org.wspcgir.strong_giraffe.model.ExerciseVariation
import org.wspcgir.strong_giraffe.model.Location
import org.wspcgir.strong_giraffe.model.ids.ExerciseVariationId
import org.wspcgir.strong_giraffe.model.previewLocations
import org.wspcgir.strong_giraffe.model.previewMuscles
import org.wspcgir.strong_giraffe.repository.AppRepository
import org.wspcgir.strong_giraffe.ui.theme.StrongGiraffeTheme
import org.wspcgir.strong_giraffe.util.Maybe
import org.wspcgir.strong_giraffe.util.Value
import org.wspcgir.strong_giraffe.views.ModalDrawerScaffold

abstract class EditExercisePageViewModel() : ViewModel() {
    abstract val exercise: Value<Exercise?>
    abstract val muscles: State<List<Muscle>>
    abstract val variations: State<List<Value<ExerciseVariation>>>
    abstract val locations: State<List<Location>>

    abstract fun init(id: ExerciseId)
    abstract fun backPage()
    abstract fun redirectToCreateMuscle()
    abstract fun delete()
    abstract fun saveVariation(id: ExerciseVariationId)
    abstract fun saveExercise()
    abstract fun addNewVariation()
}


class EditExercisePageViewModelImpl(
    private val repo: AppRepository,
    private val nav: DestinationsNavigator
) : EditExercisePageViewModel() {

    private var variationsMap:
            Map<ExerciseVariationId, Value<ExerciseVariation>> = emptyMap()

    private val exerciseMut: Value<Exercise?> = Value(mutableStateOf(null))
    private val variationsMut: MutableState<List<Value<ExerciseVariation>>> =
        mutableStateOf(emptyList())
    private val musclesMut: MutableState<List<Muscle>> = mutableStateOf(emptyList())
    private val locationsMut: MutableState<List<Location>> = mutableStateOf(emptyList())

    override val exercise: Value<Exercise?>
        get() = exerciseMut

    override val muscles: State<List<Muscle>>
        get() = musclesMut

    override val variations: State<List<Value<ExerciseVariation>>>
        get() = variationsMut

    override val locations: State<List<Location>>
        get() = locationsMut

    override fun init(id: ExerciseId) {
        viewModelScope.launch {
            exerciseMut.state.value = repo.getExerciseFromId(id)
            val vars = repo.getVariationsForExercise(id)
            Log.d("EditExercisePageViewModelImpl.init", "$vars")
            variationsMap = vars
                .foldRight(emptyMap()) { x, xs -> xs.plus(x.id to Value(mutableStateOf(x))) }
            rebuildVariationsListFromMap()
            musclesMut.value = repo.getMuscles()
            locationsMut.value = repo.getLocations()
            Log.i("DEBUG", "EditExercisePageViewModel initialized")
            Log.i("DEBUG", "The exercise is ${exerciseMut.state.value}")
        }
    }

    override fun backPage() {
        nav.popBackStack()
    }

    override fun redirectToCreateMuscle() {
        viewModelScope.launch {
            val new = repo.newMuscle()
            nav.navigate(
                EditMusclePageDestination(
                    EditMusclePageNavArgs(new.id, new.name)
                )
            )
        }
    }

    override fun delete() {
        exercise.state.value?.let {
            viewModelScope.launch {
                repo.deleteExercise(it.id)
            }
        }
        nav.popBackStack()
    }

    override fun saveVariation(id: ExerciseVariationId) {
        val variation = variationsMap[id]
        if (variation != null) {
            val v = variation.state.value
            viewModelScope.launch {
                repo.updateVariation(v.id, v.name, v.location)
            }
        }
    }

    override fun saveExercise() {
        viewModelScope.launch {
            val e = exercise.state.value
            e?.let { repo.updateExercise(it.id, it.name, it.muscle) }
        }
    }

    override fun addNewVariation() {
        exercise.state.value?.let {
            viewModelScope.launch {
                val variation = repo.newExerciseVariation(it.id)
                variationsMap = variationsMap.plus(variation.id to Value(mutableStateOf(variation)))
                rebuildVariationsListFromMap()
            }
        }
    }

    private fun rebuildVariationsListFromMap() {
        variationsMut.value = variationsMap.values.toList()
    }
}


@Composable
@Destination
fun EditExercisePage(id: ExerciseId, view: EditExercisePageViewModel = hiltViewModel()) {
    view.init(id)
    if (view.muscles.value.isEmpty()) {
        RequiredDataRedirect(missing = "Muscle") {
            view.redirectToCreateMuscle()
        }
    } else {
        if (view.exercise.state.value != null) {
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

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun Page(view: EditExercisePageViewModel) {
    val keyboardController = LocalSoftwareKeyboardController.current
    ModalDrawerScaffold(
        title = "Edit Exercise",
        actionButton = { ActionButtons(view) },
        drawerContent = { Drawer(view) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Column { NameField(view, keyboardController) }
            Column { MuscleField(view) }
            Column { Variations(view) }
        }
    }
}

@Composable
private fun ActionButtons(view: EditExercisePageViewModel) {
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
                view.saveExercise()
                view.backPage()
            }
        ) {
            Icon(Icons.Default.Done, contentDescription = "Save Location")
        }
    }
}

@Composable
private fun Drawer(view: EditExercisePageViewModel) {
    Button(onClick = view::delete) {
        Text("Delete")
        Spacer(modifier = Modifier.fillMaxWidth(0.03f))
        Icon(Icons.Default.Delete, contentDescription = "delete exercise")
    }
}

@Composable
private fun Variations(view: EditExercisePageViewModel) {
    Text("Variations", fontSize = FIELD_NAME_FONT_SIZE)
    LazyColumn(
        modifier = Modifier.fillMaxWidth(0.8f),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        items(view.variations.value) { variation ->
            VariationCard(
                value = variation.state.value,
                locations = view.locations.value,
                onChangeLocation = { location ->
                    variation.modify {
                        it.copy(location = location?.id)
                    }
                    view.saveVariation(variation.state.value.id)
                    Log.d("EditExercisePage", "Location Updated")
                },
                onChangeName = { name ->
                    variation.modify { it.copy(name = name) }
                    view.saveVariation(variation.state.value.id)
                }
            )
        }
    }
}

@Composable
private fun MuscleField(view: EditExercisePageViewModel) {
    val muscle = remember {
        view.exercise.child(
            { it?.muscle },
            { p, c -> c?.let { p?.copy(muscle = it) } ?: p }
        )
    }
    Text("Muscle", fontSize = FIELD_NAME_FONT_SIZE)
    LargeDropDownFromList(
        modifier = Modifier.fillMaxWidth(0.8f),
        items = view.muscles.value,
        itemToString = { it.name },
        onItemSelected = {
            muscle.set(it.id)
            view.saveExercise()
        },
        selectedIndex = view.muscles.value.map { it.id }.indexOf(muscle.state.value)
    )
}

@Composable
private fun NameField(
    view: EditExercisePageViewModel,
    keyboardController: SoftwareKeyboardController?
) {
    Text("Name", fontSize = FIELD_NAME_FONT_SIZE)
    val exerciseName = remember {
        view.exercise.child(
            { it?.name ?: "N/A" },
            { parent, child -> parent?.copy(name = child) }
        )
    }
    TextField(
        modifier = Modifier
            .fillMaxWidth(0.8f)
            .onFocusChanged {
                if (!it.isFocused) {
                    view.saveExercise()
                }
            },
        value = exerciseName.state.value,
        onValueChange = { exerciseName.set(it) },
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
        keyboardActions = KeyboardActions(
            onDone = {
                view.saveExercise()
                keyboardController?.hide()
            }
        )
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VariationCard(
    value: ExerciseVariation,
    locations: List<Location>,
    onChangeLocation: (Location?) -> Unit,
    onChangeName: (String) -> Unit
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    key(value.name, value.location) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(10.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                var name by remember { mutableStateOf(value.name) }
                TextField(
                    name,
                    onValueChange = { name = it },
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                    keyboardActions = KeyboardActions(
                        onDone = {
                            keyboardController?.hide()
                            onChangeName(name)
                        }
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .onFocusChanged {
                            if (!it.isFocused) {
                                onChangeName(name)
                            }
                        }
                )
                val locationsAndNull =
                    listOf(Maybe.Nothing<Location>()).plus(locations.map { Maybe.Just(it) })
                LargeDropDownFromList(
                    items = locationsAndNull,
                    selectedIndex = locationsAndNull
                        .map { it.over { location -> location.id }.toNull() }
                        .indexOf(value.location),
                    itemToString = {
                        when (it) {
                            is Maybe.Nothing -> "Any Location"
                            is Maybe.Just -> it.some.name
                        }
                    },
                    onItemSelected = { onChangeLocation(it.toNull()) },
                    modifier = Modifier
                )
            }
        }
    }
}


@Composable
@Preview
private fun Unfilled() {
    val exercise = ExerciseId("exercise")
    StrongGiraffeTheme {
        EditExercisePage(
            exercise,
            previewViewModel(null, emptyList())
        )
    }
}

@Composable
@Preview
private fun Filled() {
    val exercise = ExerciseId("exercise")
    StrongGiraffeTheme {
        EditExercisePage(
            exercise,
            previewViewModel(
                Exercise(exercise, "Action", previewMuscles.random().id),
                listOf(
                    ExerciseVariation(
                        id = ExerciseVariationId("a"),
                        name = "Bench",
                        exercise = exercise,
                        location = previewLocations.random().id
                    ),
                    ExerciseVariation(
                        id = ExerciseVariationId("b"),
                        name = "Dumbbell",
                        exercise = exercise,
                        location = null
                    )
                )
            )
        )
    }
}

@Composable
private fun previewViewModel(
    exercise: Exercise?,
    variations: List<ExerciseVariation>
) = object : EditExercisePageViewModel() {
    override val exercise: Value<Exercise?>
        get() = Value(mutableStateOf(exercise))
    override val muscles: State<List<Muscle>>
        get() = mutableStateOf(previewMuscles)
    override val variations: State<List<Value<ExerciseVariation>>>
        get() = mutableStateOf(
            variations.map { Value(mutableStateOf(it)) }
        )
    override val locations: State<List<Location>>
        get() = mutableStateOf(previewLocations)

    override fun init(id: ExerciseId) {}
    override fun backPage() {}
    override fun redirectToCreateMuscle() {}
    override fun delete() {}
    override fun saveVariation(id: ExerciseVariationId) {}
    override fun saveExercise() {}
    override fun addNewVariation() {}
}