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
import com.example.stronggiraffe.views.LargeDropDownFromList
import com.example.stronggiraffe.model.Equipment
import com.example.stronggiraffe.model.Exercise
import com.example.stronggiraffe.model.Intensity
import com.example.stronggiraffe.model.Location
import com.example.stronggiraffe.model.ids.EquipmentId
import com.example.stronggiraffe.model.ids.ExerciseId
import com.example.stronggiraffe.model.ids.LocationId
import com.example.stronggiraffe.model.ids.MuscleId
import com.ramcosta.composedestinations.annotation.Destination

@OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class)
@Composable
@Destination
fun EditSetPage(
    submit: (Location, Exercise, Equipment, Int, Intensity, String) -> Unit =
        { _, _, _, _, _, _ -> },
    locations: List<Location> =
        listOf(
            Location(LocationId("locationA"), "24 Hour Parkmoore"),
            Location(LocationId("locationB"), "Fruitdale Apt. Gym"),
        ),
    equipment: List<Equipment> =
        listOf(
            Equipment(EquipmentId("equipA"), "Barbell", LocationId("locationA")),
            Equipment(EquipmentId("equipB"), "Bicep Curl A", LocationId("locationA")),
        ),
    exercises: List<Exercise> =
        listOf(
            Exercise(ExerciseId("a"), "Lat Pull Down", MuscleId("0")),
            Exercise(ExerciseId("b"), "Squats", MuscleId("1")),
        )
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    if (locations.isNotEmpty() && exercises.isNotEmpty()) {

        var selectedLocation by remember { mutableStateOf(locations[0]) }
        var equipmentForLocation by remember {
            mutableStateOf(equipment.filter { it.location == selectedLocation.id })
        }
        var selectedEquipment by remember { mutableStateOf(equipmentForLocation.first()) }
        var selectedExercise by remember { mutableStateOf(exercises[0]) }
        var selectedReps by remember { mutableStateOf(10) }
        var selectedIntensity by remember { mutableStateOf(Intensity.Normal as Intensity) }
        var comment by remember { mutableStateOf("") }

        Scaffold(
            floatingActionButton = {
                FloatingActionButton(
                    onClick = {
                        submit(
                            selectedLocation,
                            selectedExercise,
                            selectedEquipment,
                            selectedReps,
                            selectedIntensity,
                            comment
                        )
                    }
                ) {
                    Icon(Icons.Default.Done, contentDescription = "Save Set")
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
                    modifier = Modifier.fillMaxWidth(0.8f)
                )
                Text("Exercise")
                LargeDropDownFromList(
                    items = exercises,
                    label = selectedExercise.name,
                    itemToString = { it.name },
                    onItemSelected = { selectedExercise = it; },
                    modifier = Modifier.fillMaxWidth(0.8f)
                )
                Text("Equipment")
                LargeDropDownFromList(
                    items = equipmentForLocation,
                    label = selectedEquipment.name,
                    itemToString = { it.name },
                    onItemSelected = { selectedEquipment = it; },
                    modifier = Modifier.fillMaxWidth(0.8f)
                )
                Text("Reps")
                LargeDropDownFromList(
                    items = 0.rangeTo(30).toList(),
                    label = selectedReps.toString(),
                    itemToString = Int::toString,
                    onItemSelected = { selectedReps = it; },
                    modifier = Modifier.fillMaxWidth(0.8f)
                )
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
                    modifier = Modifier.fillMaxWidth(0.8f)
                )
                Text("Comment")
                TextField(
                    modifier = Modifier.fillMaxWidth(0.8f),
                    value = comment,
                    onValueChange = { comment = it; },
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                    keyboardActions = KeyboardActions(onDone = { keyboardController?.hide() })
                )
            }
        }
    }
}