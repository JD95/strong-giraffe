package org.wspcgir.strong_giraffe.destinations

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import org.wspcgir.strong_giraffe.model.*
import org.wspcgir.strong_giraffe.model.ids.*
import org.wspcgir.strong_giraffe.repository.AppRepository
import org.wspcgir.strong_giraffe.views.*
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import java.time.Instant

data class EditSetPageNavArgs(val id: SetId)

class EditSetPageViewModel(
    private val setId: SetId,
    private val repo: AppRepository,
    private val dest: DestinationsNavigator,
    private val inProgressMut: MutableState<WorkoutSet>,
    private val equipmentForLocationMut: MutableState<List<Equipment>>,
    val locations: List<Location>,
    val exercises: List<Exercise>,
    val equipment: List<Equipment>,
): ViewModel() {
    fun submit() {
        viewModelScope.launch {
            repo.updateWorkoutSet(
                id = setId,
                exercise = inProgress.value.exercise,
                location = inProgress.value.location,
                equipment = inProgress.value.equipment,
                reps = inProgress.value.reps,
                weight = inProgress.value.weight,
                intensity = inProgress.value.intensity,
                comment = inProgress.value.comment,
                time = inProgressMut.value.time
            )
        }
        dest.popBackStack()
    }

    fun changeLocation(location: LocationId) {
        equipmentForLocationMut.value = equipment.filter { it.location == location }
        viewModelScope.launch {
            val replacement = repo.latestSetForExerciseAtLocationExcluding(
                inProgress.value.id,
                inProgress.value.location,
                inProgress.value.exercise,
            )
            if (replacement != null) {
               inProgressMut.value = inProgress.value.copy(
                   location = replacement.location,
                   equipment = replacement.equipment,
                   exercise = replacement.exercise,
                   reps = replacement.reps,
                   weight = replacement.weight,
               )
            } else {
                inProgressMut.value = inProgress.value.copy(
                    location = location,
                    equipment = equipmentForLocationMut.value.first().id,
                )
            }

        }
    }

    fun changeExercise(exercise: ExerciseId) {
        viewModelScope.launch {
            val replacement = repo.latestSetForExerciseAndEquipmentAtLocationExcluding(
                inProgress.value.id,
                inProgress.value.location,
                exercise,
                inProgress.value.equipment
            )
            if (replacement != null) {
                inProgressMut.value = inProgress.value.copy(
                    exercise = replacement.exercise,
                    reps = replacement.reps,
                    weight = replacement.weight,
                )
            } else {
                inProgressMut.value = inProgress.value.copy(exercise = exercise)
            }
        }
    }

    fun changeEquipment(equipment: EquipmentId) {
        viewModelScope.launch {
            val replacement = repo.latestSetForExerciseAndEquipmentAtLocationExcluding(
                inProgress.value.id,
                inProgress.value.location,
                inProgress.value.exercise,
                equipment
            )
            if (replacement != null) {
                inProgressMut.value = inProgressMut.value.copy(
                    equipment = replacement.equipment,
                    reps = replacement.reps,
                    weight = replacement.weight,
                )
            } else {
                inProgressMut.value = inProgress.value.copy(equipment = equipment)
            }
        }
    }

    fun changeReps(new: Reps){
        inProgressMut.value = inProgress.value.copy(reps = new)
    }

    fun changeWeight(new: Weight){
        inProgressMut.value = inProgress.value.copy(weight = new)
    }

    fun changeIntensity(new: Intensity){
        inProgressMut.value = inProgress.value.copy(intensity = new)
    }

    fun changeComment(new: Comment){
        inProgressMut.value = inProgress.value.copy(comment = new)
    }

    fun delete() {
        viewModelScope.launch {
            repo.deleteWorkoutSet(setId)
        }
        dest.popBackStack()
    }

    val inProgress: State<WorkoutSet>
        get() = inProgressMut
    val equipmentForExercise: State<List<Equipment>>
        get() = equipmentForLocationMut
}

@Composable
fun RegisterEditSetPage(
    navArgs: EditSetPageNavArgs,
    repo: AppRepository,
    dest: DestinationsNavigator
) {

    var locations by remember { mutableStateOf<List<Location>>(emptyList()) }
    var equipment by remember { mutableStateOf<List<Equipment>>(emptyList()) }
    var exercises by remember { mutableStateOf<List<Exercise>>(emptyList()) }
    val inProgress = remember { mutableStateOf<WorkoutSet?>(null) }

    LaunchedEffect(locations, equipment, exercises) {
        locations = repo.getLocationsWithEquipment()
        equipment = repo.getEquipment()
        exercises = repo.getExercises()
        inProgress.value = repo.getSetFromId(navArgs.id)
        Log.i("SET", "weight is now '${inProgress.value?.weight}")
    }

    if (inProgress.value != null) {
        EditSetPage(EditSetPageViewModel(
            setId = navArgs.id,
            inProgressMut = remember { mutableStateOf(inProgress.value!!) },
            equipmentForLocationMut = remember {
                mutableStateOf(equipment.filter { it.location == inProgress.value?.location })
            },
            repo = repo,
            dest = dest,
            locations = locations,
            exercises = exercises,
            equipment = equipment
        ))
    }
}

@Composable
@Destination(navArgsDelegate = EditSetPageNavArgs::class)
fun EditSetPage(view: EditSetPageViewModel) {
    Page(
        submit = view::submit,
        delete = view::delete,
        changeLocation = view::changeLocation,
        changeExercise = view::changeExercise,
        changeEquipment = view::changeEquipment,
        changeReps = view::changeReps,
        changeWeight = view::changeWeight,
        changeIntensity = view::changeIntensity,
        changeComment = view::changeComment,
        starting = view.inProgress,
        locations = view.locations,
        equipment = view.equipmentForExercise,
        exercises = view.exercises,
    )
}

@OptIn(ExperimentalComposeUiApi::class, ExperimentalMaterial3Api::class)
@Composable
fun Page(
    starting: State<WorkoutSet>,
    submit: () -> Unit,
    delete: () -> Unit,
    changeLocation: (LocationId) -> Unit,
    changeExercise: (ExerciseId) -> Unit,
    changeEquipment: (EquipmentId) -> Unit,
    changeReps: (Reps) -> Unit,
    changeWeight: (Weight) -> Unit,
    changeIntensity: (Intensity) -> Unit,
    changeComment: (Comment) -> Unit,
    locations: List<Location>,
    equipment: State<List<Equipment>>,
    exercises: List<Exercise>,
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    if (locations.isNotEmpty() && exercises.isNotEmpty()) {
        var validReps by remember { mutableStateOf(true) }
        var validWeight by remember { mutableStateOf(true) }

        Scaffold(
            floatingActionButton = {
                FloatingActionButton(onClick = submit) {
                    if (validReps && validWeight) {
                        Icon(Icons.Default.Done, contentDescription = "Save Set")
                    } else {
                        Icon(Icons.Default.Warning, contentDescription = "Invalid fields")
                    }
                }
            }
        ) { innerPadding ->

            Column(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
            ) {
                Text("Location")
                LargeDropDownFromList(
                    items = locations,
                    label = "",
                    itemToString = { it.name },
                    onItemSelected = {
                        if (starting.value.location != it.id) {
                            changeLocation(it.id)
                        }
                    },
                    modifier = Modifier.fillMaxWidth(0.8f),
                    selectedIndex = locations.indexOfFirst { it.id == starting.value.location }
                )
                Text("Exercise")
                LargeDropDownFromList(
                    items = exercises,
                    label = "",
                    itemToString = { it.name },
                    onItemSelected = { changeExercise(it.id) },
                    modifier = Modifier.fillMaxWidth(0.8f),
                    selectedIndex = exercises.indexOfFirst { it.id == starting.value.exercise }
                )
                Text("Equipment")
                LargeDropDownFromList(
                    items = equipment.value,
                    label = "",
                    itemToString = { it.name },
                    onItemSelected = { changeEquipment(it.id) },
                    modifier = Modifier.fillMaxWidth(0.8f),
                    selectedIndex = equipment.value.indexOfFirst { it.id == starting.value.equipment }
                )
                Text("Reps")
                IntField(
                    modifier = Modifier.fillMaxWidth(0.8f),
                    start = starting.value.reps.value
                ) { it ->
                    validReps = it != null
                    if (it != null) {
                        changeReps(Reps(it))
                    }
                }
                Text("Weight")
                IntField(
                    modifier = Modifier.fillMaxWidth(0.8f),
                    start = starting.value.weight.value
                ) { it ->
                    validWeight = it != null
                    if (it != null) {
                        changeWeight(Weight(it))
                    }
                }
                Text("Intensity")
                LargeDropDownFromList(
                    items = listOf(
                        Intensity.NoActivation,
                        Intensity.Easy,
                        Intensity.Normal,
                        Intensity.EarlyFailure,
                        Intensity.Pain
                    ),
                    label = "",
                    itemToString = Intensity::toString,
                    onItemSelected = changeIntensity,
                    modifier = Modifier.fillMaxWidth(0.8f),
                    selectedIndex = Intensity.toInt(starting.value.intensity)
                )
                Text("Comment")
                TextField(
                    modifier = Modifier.fillMaxWidth(0.8f),
                    value = starting.value.comment.value,
                    onValueChange = { changeComment(Comment(it)) },
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                    keyboardActions = KeyboardActions(onDone = { keyboardController?.hide() })
                )
                Spacer(modifier = Modifier.fillMaxHeight(0.1f))
                Button(onClick = delete) {
                    Text("Delete")
                    Spacer(modifier = Modifier.fillMaxWidth(0.03f))
                    Icon(Icons.Default.Delete, contentDescription = "delete set")
                }
            }
        }
    }
}

@Preview
@Composable
private fun Preview() {
    Page(
        starting = remember { mutableStateOf(WorkoutSet(
            id = SetId("a"),
            exercise = ExerciseId("a"),
            equipment = EquipmentId("equipA"),
            location = LocationId("locationA"),
            reps = Reps(0),
            weight = Weight(0),
            time = Instant.now(),
            intensity = Intensity.Normal,
            comment = Comment("")
        ))},
        submit = { },
        delete = { },
        changeLocation = { },
        changeExercise = { },
        changeEquipment = { },
        changeReps = { },
        changeWeight = { },
        changeIntensity = { },
        changeComment = { },
        locations = listOf(
            Location(LocationId("locationA"), "24 Hour Parkmoore"),
            Location(LocationId("locationB"), "Fruitdale Apt. Gym"),
        ),
        equipment = remember {
            mutableStateOf(
                listOf(
                    Equipment(EquipmentId("equipA"), "Barbell", LocationId("locationA")),
                    Equipment(EquipmentId("equipB"), "Bicep Curl A", LocationId("locationA")),
                )
            )
        },
        exercises = listOf(
            Exercise(ExerciseId("a"), "Lat Pull Down", MuscleId("0")),
            Exercise(ExerciseId("b"), "Squats", MuscleId("1")),
        )
    )
}