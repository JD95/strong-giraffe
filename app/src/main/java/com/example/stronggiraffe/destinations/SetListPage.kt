package com.example.stronggiraffe.destinations

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.stronggiraffe.model.*
import com.example.stronggiraffe.model.ids.*
import com.example.stronggiraffe.repository.AppRepository
import com.example.stronggiraffe.views.*
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import kotlinx.coroutines.launch

abstract class SetListPageViewModel: ViewModel() {
    abstract val sets: List<SetSummary>
    abstract val templateLocation: LocationId
    abstract val templateEquipment: EquipmentId
    abstract val templateExercise: ExerciseId
    abstract fun new()
    abstract fun goto(id: SetSummary)
}

@Composable
fun RegisterSetListPage(repo: AppRepository, dest: DestinationsNavigator) {
    var templateLocation by remember { mutableStateOf<LocationId?>(null) }
    var templateEquipment by remember { mutableStateOf<EquipmentId?>(null) }
    var templateMuscle by remember { mutableStateOf<MuscleId?>(null) }
    var templateExercise by remember { mutableStateOf<ExerciseId?>(null) }
    var setSummaries by remember { mutableStateOf<List<SetSummary>>(emptyList()) }
    val scope = rememberCoroutineScope()

    LaunchedEffect(templateLocation, templateEquipment, templateMuscle, templateExercise) {
        val equipment = repo.getEquipment().firstOrNull()
        templateLocation = equipment?.location ?: repo.getLocations().firstOrNull()?.id
        templateEquipment = equipment?.id
        val exercise = repo.getExercises().firstOrNull()
        templateMuscle = exercise?.muscle ?: repo.getMuscles().firstOrNull()?.id
        templateExercise = exercise?.id
        setSummaries = repo.getSetSummaries()
    }

    if (templateLocation == null) {
        LocationEditRedirection(scope, repo, dest)
    } else if (templateEquipment == null) {
        EquipmentEditRedirection(templateLocation!!, scope, repo, dest)
    } else if (templateMuscle == null) {
        MuscleEditRedirection(scope, repo, dest)
    } else if (templateExercise == null) {
        ExerciseEditRedirection(templateMuscle!!, scope, repo, dest)
    } else {

        SetListPage(object: SetListPageViewModel() {
            override val sets: List<SetSummary>
                get() = setSummaries
            override val templateLocation: LocationId
                get() = templateLocation!!
            override val templateEquipment: EquipmentId
                get() = templateEquipment!!
            override val templateExercise: ExerciseId
                get() = templateExercise!!

            override fun new() {
                viewModelScope.launch {
                    val new: WorkoutSet = repo.newWorkoutSet(
                        templateLocation!!,
                        templateEquipment!!,
                        templateExercise!!
                    )
                    dest.navigate(
                        EditSetPageDestination(
                            EditSetPageNavArgs(id = new.id)
                        )
                    )
                }
            }

            override fun goto(id: SetSummary) {
                viewModelScope.launch {
                    dest.navigate(EditSetPageDestination(EditSetPageNavArgs(id = id.id)))
                }
            }
        })
    }
}

@Composable
@Destination
fun SetListPage(view: SetListPageViewModel) {
    Page(
        sets = view.sets,
        gotoNew = view::new,
        goto = view::goto
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun Page(
    sets: List<SetSummary>,
    gotoNew: () -> Unit,
    goto: (SetSummary) -> Unit
) {
    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { gotoNew() }
            ) {
                Icon(Icons.Default.Add, contentDescription = "Create New")
            }
        }
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text("Sets", fontSize = PAGE_TITLE_FONTSIZE)
            Spacer(modifier = Modifier.fillMaxHeight(0.3f))
            if (sets.isNotEmpty()) {
                sets.forEach { item ->
                    Button(
                        colors = ButtonDefaults.buttonColors(
                            containerColor = intensityColor(item.intensity)
                        ),
                        onClick = { goto(item) }
                    ) {
                        Text("${item.exerciseName} | ${item.reps} | ${item.weight}")
                    }
                }
            } else {
                Text("There's nothing here yet")
            }
        }
    }


}