package org.wspcgir.strong_giraffe.destinations

import android.util.Log
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
import java.time.Instant

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
                    val set = repo.newWorkoutSet(
                        templateLocation!!,
                        templateEquipment!!,
                        templateExercise!!
                    )
                    val latest = repo.latestSetNot(set.id)
                    if (latest != null) {
                        Log.i("NEW SET", "Using previous set '${latest.id}'")
                        repo.updateWorkoutSet(
                            original = set,
                            location = latest.location,
                            exercise = latest.exercise,
                            equipment = latest.equipment,
                            reps = latest.reps,
                            weight = latest.weight
                        )
                    } else {
                        Log.i("NEW SET", "No previous set available.")
                    }
                    dest.navigate(
                        EditSetPageDestination(
                            EditSetPageNavArgs(id = set.id)
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

@Composable
private fun Page(
    sets: List<SetSummary>,
    gotoNew: () -> Unit,
    goto: (SetSummary) -> Unit
) {
    EditPageList(
        title = "Sets",
        items = sets,
        gotoNewPage = gotoNew,
        gotoEditPage = goto,
        sortBy = { x, y -> y.time.compareTo(x.time) },
        rowRender = { onClick, inner, item ->
            Button(
                colors = ButtonDefaults.buttonColors(
                    containerColor = intensityColor(item.intensity)
                ),
                onClick = { onClick(item) }
            ) {
                inner(item)
            }
        }
    ) {
        Text("${it.exerciseName} | ${it.reps} | ${it.weight}")
    }
}

@Preview
@Composable
private fun Preview() {
    val template =
        SetSummary(
            id = SetId("a"),
            reps = Reps(10),
            weight = Weight(140),
            time = Instant.now(),
            intensity = Intensity.Easy,
            exerciseName = "Bicep Curls",
            exerciseId = ExerciseId("a")
        )
    Page(
        sets = listOf(
            template.copy(
                time = template.time.minusSeconds(60),
                reps = Reps(5),
                intensity = Intensity.NoActivation
            ),
            template.copy(
                time = template.time.minusSeconds(120),
                reps = Reps(20),
                intensity = Intensity.Pain
            ),
            template,
        ),
        gotoNew = { },
        goto = { }
    )
}