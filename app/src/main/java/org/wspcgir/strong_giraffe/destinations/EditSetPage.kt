package org.wspcgir.strong_giraffe.destinations

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
import kotlinx.coroutines.launch

data class EditSetPageNavArgs(val id: SetId)

abstract class EditSetPageViewModel : ViewModel() {

    abstract fun submit(
        location: Location,
        exercise: Exercise,
        equipment: Equipment,
        reps: Reps,
        weight: Weight,
        intensity: Intensity,
        comment: Comment
    )

    abstract fun delete()

    abstract val starting: WorkoutSet
    abstract val locations: List<Location>
    abstract val equipment: List<Equipment>
    abstract val exercises: List<Exercise>
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
    var starting by remember { mutableStateOf<WorkoutSet?>(null) }

    LaunchedEffect(locations, equipment, exercises) {
        locations = repo.getLocations()
        equipment = repo.getEquipment()
        exercises = repo.getExercises()
        starting = repo.getSetFromId(navArgs.id)
    }

    if (starting != null) {
        EditSetPage(object : EditSetPageViewModel() {
            override fun submit(
                location: Location,
                exercise: Exercise,
                equipment: Equipment,
                reps: Reps,
                weight: Weight,
                intensity: Intensity,
                comment: Comment
            ) {
                viewModelScope.launch {
                    repo.updateWorkoutSet(
                        id = navArgs.id,
                        exercise = exercise.id,
                        location = location.id,
                        equipment = equipment.id,
                        reps = reps,
                        weight = weight,
                        intensity = intensity,
                        comment = comment,
                        time = starting!!.time
                    )
                }
                dest.popBackStack()
            }

            override fun delete() {
                viewModelScope.launch {
                    repo.deleteWorkoutSet(navArgs.id)
                }
                dest.popBackStack()
            }

            override val starting: WorkoutSet
                get() = starting!!

            override val locations: List<Location>
                get() = locations
            override val equipment: List<Equipment>
                get() = equipment
            override val exercises: List<Exercise>
                get() = exercises
        })
    }
}

@Composable
@Destination(navArgsDelegate = EditSetPageNavArgs::class)
fun EditSetPage(view: EditSetPageViewModel) {
    Page(
        submit = view::submit,
        delete = view::delete,
        starting = view.starting,
        locations = view.locations,
        equipment = view.equipment,
        exercises = view.exercises,
    )
}


@OptIn(ExperimentalComposeUiApi::class, ExperimentalMaterial3Api::class)
@Composable
fun Page(
    starting: WorkoutSet,
    submit: (Location, Exercise, Equipment, Reps, Weight, Intensity, Comment) -> Unit,
    delete: () -> Unit,
    locations: List<Location>,
    equipment: List<Equipment>,
    exercises: List<Exercise>,
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    if (locations.isNotEmpty() && exercises.isNotEmpty()) {

        var selectedLocation by remember {
            mutableStateOf(locations.first { it.id == starting.location})
        }
        var equipmentForLocation by remember {
            mutableStateOf(equipment.filter { it.location == selectedLocation.id })
        }
        var selectedEquipment by remember {
            mutableStateOf(equipment.first { it.id == starting.equipment } )
        }
        var selectedExercise by remember {
            mutableStateOf(exercises.first { it.id == starting.exercise })
        }
        var selectedReps by remember { mutableStateOf(starting.reps) }
        var selectedWeight by remember { mutableStateOf(starting.weight) }
        var selectedIntensity by remember { mutableStateOf(starting.intensity) }
        var comment by remember { mutableStateOf(starting.comment) }
        var validReps by remember { mutableStateOf(true) }
        var validWeight by remember { mutableStateOf(true) }

        Scaffold(
            floatingActionButton = {
                FloatingActionButton(
                    onClick = {
                        if (validReps && validWeight) {
                            submit(
                                selectedLocation,
                                selectedExercise,
                                selectedEquipment,
                                selectedReps,
                                selectedWeight,
                                selectedIntensity,
                                comment
                            )
                        }
                    }
                ) {
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
                    label = selectedLocation.name,
                    itemToString = { it.name },
                    onItemSelected = {
                        if (selectedLocation != it) {
                            selectedLocation = it
                            equipmentForLocation = equipment.filter { e ->
                                e.location == selectedLocation.id
                            }
                            selectedEquipment = equipmentForLocation.first()
                        }
                    },
                    modifier = Modifier.fillMaxWidth(0.8f),
                    selectedIndex = locations.indexOf(selectedLocation)
                )
                Text("Exercise")
                LargeDropDownFromList(
                    items = exercises,
                    label = selectedExercise.name,
                    itemToString = { it.name },
                    onItemSelected = { selectedExercise = it; },
                    modifier = Modifier.fillMaxWidth(0.8f),
                    selectedIndex = exercises.indexOf(selectedExercise)
                )
                Text("Equipment")
                LargeDropDownFromList(
                    items = equipmentForLocation,
                    label = selectedEquipment.name,
                    itemToString = { it.name },
                    onItemSelected = { selectedEquipment = it; },
                    modifier = Modifier.fillMaxWidth(0.8f),
                    selectedIndex = equipment.indexOf(selectedEquipment)
                )
                Text("Reps")
                IntField(
                    modifier = Modifier.fillMaxWidth(0.8f),
                    start = starting.reps.value
                ) { it ->
                    validReps = it != null
                    if (it != null) {
                        selectedReps = Reps(it)
                    }
                }
                Text("Weight")
                IntField(
                    modifier = Modifier.fillMaxWidth(0.8f),
                    start = starting.weight.value
                ) { it ->
                    validWeight = it != null
                    if (it != null) {
                        selectedWeight = Weight(it)
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
                    label = selectedIntensity.toString(),
                    itemToString = Intensity::toString,
                    onItemSelected = { selectedIntensity = it; },
                    modifier = Modifier.fillMaxWidth(0.8f),
                    selectedIndex = Intensity.toInt(selectedIntensity)
                )
                Text("Comment")
                TextField(
                    modifier = Modifier.fillMaxWidth(0.8f),
                    value = comment.value,
                    onValueChange = { comment = Comment(it); },
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

    // submit: (Location, Exercise, Equipment, Int, Intensity, String) -> Unit =
    // { _, _, _, _, _, _ -> },
    // locations: List<Location> =
    // listOf(
    //     Location(LocationId("locationA"), "24 Hour Parkmoore"),
    //     Location(LocationId("locationB"), "Fruitdale Apt. Gym"),
    // ),
    // equipment: List<Equipment> =
    // listOf(
    //     Equipment(EquipmentId("equipA"), "Barbell", LocationId("locationA")),
    //     Equipment(EquipmentId("equipB"), "Bicep Curl A", LocationId("locationA")),
    // ),
    // exercises: List<Exercise> =
    // listOf(
    //     Exercise(ExerciseId("a"), "Lat Pull Down", MuscleId("0")),
    //     Exercise(ExerciseId("b"), "Squats", MuscleId("1")),
    // )
}