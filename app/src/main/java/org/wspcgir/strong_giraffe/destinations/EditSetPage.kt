package org.wspcgir.strong_giraffe.destinations

import android.content.res.Configuration.UI_MODE_NIGHT_NO
import android.content.res.Configuration.UI_MODE_NIGHT_YES
import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import kotlinx.coroutines.launch
import org.wspcgir.strong_giraffe.model.Comment
import org.wspcgir.strong_giraffe.model.Equipment
import org.wspcgir.strong_giraffe.model.Exercise
import org.wspcgir.strong_giraffe.model.Intensity
import org.wspcgir.strong_giraffe.model.Location
import org.wspcgir.strong_giraffe.model.Reps
import org.wspcgir.strong_giraffe.model.Weight
import org.wspcgir.strong_giraffe.model.WorkoutSet
import org.wspcgir.strong_giraffe.model.ids.EquipmentId
import org.wspcgir.strong_giraffe.model.ids.ExerciseId
import org.wspcgir.strong_giraffe.model.ids.LocationId
import org.wspcgir.strong_giraffe.model.ids.MuscleId
import org.wspcgir.strong_giraffe.model.ids.SetId
import org.wspcgir.strong_giraffe.repository.AppRepository
import org.wspcgir.strong_giraffe.ui.theme.StrongGiraffeTheme
import org.wspcgir.strong_giraffe.views.FIELD_NAME_FONT_SIZE
import org.wspcgir.strong_giraffe.views.IntField
import org.wspcgir.strong_giraffe.views.LargeDropDownFromList
import org.wspcgir.strong_giraffe.views.ModalDrawerScaffold
import org.wspcgir.strong_giraffe.views.PreviousSetButton
import org.wspcgir.strong_giraffe.views.intensityColor
import java.time.Instant
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter
import java.util.TimeZone

data class EditSetPageNavArgs(val id: SetId, val locked: Boolean)

val NUM_PREVIOUS_SETS = 6

class EditSetPageViewModel(
    private val setId: SetId,
    private val repo: AppRepository,
    private val dest: DestinationsNavigator,
    private val inProgressMut: MutableState<WorkoutSet>,
    private val equipmentForLocationMut: MutableState<List<Equipment>>,
    private val previousSetsMut: MutableState<List<WorkoutSet>>,
    private val lockedMut: MutableState<Boolean>,
    val locations: List<Location>,
    val exercises: List<Exercise>,
    val equipment: List<Equipment>,
) : ViewModel() {
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
        val newEquipment = equipmentForLocationMut.value.first().id
        viewModelScope.launch {
            inProgressMut.value = inProgress.value.copy(location = location)
            changeEquipment(newEquipment)
        }
    }

    fun changeExercise(exercise: ExerciseId) {
        viewModelScope.launch {
            previousSetsMut.value = repo.setForExerciseAndEquipmentBefore(
                inProgress.value.time,
                exercise,
                inProgress.value.equipment,
                NUM_PREVIOUS_SETS
            )
            inProgressMut.value = inProgress.value.copy(exercise = exercise)
        }
    }

    fun changeEquipment(equipment: EquipmentId) {
        viewModelScope.launch {
            previousSetsMut.value = repo.setForExerciseAndEquipmentBefore(
                inProgress.value.time,
                inProgress.value.exercise,
                equipment,
                NUM_PREVIOUS_SETS
            )
            inProgressMut.value = inProgress.value.copy(equipment = equipment)
        }
    }

    fun changeReps(new: Reps) {
        inProgressMut.value = inProgress.value.copy(reps = new)
    }

    fun changeWeight(new: Weight) {
        inProgressMut.value = inProgress.value.copy(weight = new)
    }

    fun changeIntensity(new: Intensity) {
        inProgressMut.value = inProgress.value.copy(intensity = new)
    }

    fun changeComment(new: Comment) {
        inProgressMut.value = inProgress.value.copy(comment = new)
    }

    fun delete() {
        viewModelScope.launch {
            repo.deleteWorkoutSet(setId)
        }
        dest.popBackStack()
    }

    fun gotoSet(set: WorkoutSet) {
        dest.navigate(EditSetPageDestination(EditSetPageNavArgs(set.id, true)))
    }

    fun toggleSetLock(new: Boolean) {
       lockedMut.value = new
    }

    val locked: State<Boolean>
        get() = lockedMut
    val inProgress: State<WorkoutSet>
        get() = inProgressMut

    val equipmentForExercise: State<List<Equipment>>
        get() = equipmentForLocationMut

    val previousSets: State<List<WorkoutSet>>
        get() = previousSetsMut
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
    val previousSets = remember { mutableStateOf<List<WorkoutSet>>(emptyList()) }
    val locked = remember { mutableStateOf(navArgs.locked) }

    LaunchedEffect(locations, equipment, exercises) {
        locations = repo.getLocationsWithEquipment()
        equipment = repo.getEquipment()
        exercises = repo.getExercises()
        inProgress.value = repo.getSetFromId(navArgs.id)
        Log.i("SET", "weight is now '${inProgress.value?.weight}")
        if (inProgress.value != null)
            previousSets.value = repo.setForExerciseAndEquipmentBefore(
                cutoff = inProgress.value!!.time,
                inProgress.value!!.exercise,
                inProgress.value!!.equipment,
                NUM_PREVIOUS_SETS
            )
    }

    if (inProgress.value != null) {
        EditSetPage(
            EditSetPageViewModel(
                setId = navArgs.id,
                inProgressMut = remember { mutableStateOf(inProgress.value!!) },
                equipmentForLocationMut = remember {
                    mutableStateOf(equipment.filter { it.location == inProgress.value?.location })
                },
                repo = repo,
                dest = dest,
                locations = locations,
                exercises = exercises,
                equipment = equipment,
                previousSetsMut = previousSets,
                lockedMut = locked
            )
        )
    }
}

@Composable
@Destination(navArgsDelegate = EditSetPageNavArgs::class)
fun EditSetPage(view: EditSetPageViewModel) {
    Page(
        locked = view.locked.value,
        toggleSetLock = view::toggleSetLock,
        submit = view::submit,
        delete = view::delete,
        changeLocation = view::changeLocation,
        changeExercise = view::changeExercise,
        changeEquipment = view::changeEquipment,
        changeReps = view::changeReps,
        changeWeight = view::changeWeight,
        changeIntensity = view::changeIntensity,
        changeComment = view::changeComment,
        gotoSet = view::gotoSet,
        starting = view.inProgress,
        locations = view.locations,
        equipment = view.equipmentForExercise,
        exercises = view.exercises,
        previousSets = view.previousSets
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Page(
    locked: Boolean,
    toggleSetLock: (Boolean) -> Unit,
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
    gotoSet: (WorkoutSet) -> Unit,
    locations: List<Location>,
    equipment: State<List<Equipment>>,
    exercises: List<Exercise>,
    previousSets: State<List<WorkoutSet>>
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    if (locations.isNotEmpty() && exercises.isNotEmpty()) {
        val validReps = remember { mutableStateOf(true) }
        val validWeight = remember { mutableStateOf(true) }

        ModalDrawerScaffold(
            title = "Edit Set",
            drawerContent = {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Row(
                        modifier = Modifier
                            .padding(bottom = 10.dp)
                            .weight(0.5f),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Set Locked", modifier = Modifier.padding(end = 10.dp))
                        Switch(
                            checked = locked,
                            onCheckedChange = toggleSetLock,
                        )
                    }
                    Spacer(modifier = Modifier.weight(0.5f))
                    Button(
                        modifier = Modifier
                            .padding(bottom = 10.dp),
                        onClick = delete
                    ) {
                        Text("Delete")
                        Spacer(modifier = Modifier.fillMaxWidth(0.03f))
                        Icon(Icons.Default.Delete, contentDescription = "delete set")
                    }
                }
            },
            actionButton = {
                FloatingActionButton(onClick = submit) {
                    if (validReps.value && validWeight.value) {
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
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                Spacer(modifier = Modifier.weight(1.0f))
                val zone = TimeZone.getDefault().toZoneId()
                val date = OffsetDateTime.ofInstant(starting.value.time, zone)
                val dateFormat = DateTimeFormatter.ofPattern("MMMM dd, yyyy")
                val timeFormat = DateTimeFormatter.ofPattern("HH:MM:SS")
                Text(date.format(dateFormat), fontSize = FIELD_NAME_FONT_SIZE)
                Text(date.format(timeFormat), fontSize = FIELD_NAME_FONT_SIZE)
                Card() {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.padding(10.dp)
                    ) {
                        LargeDropDownFromList(
                            items = locations,
                            label = "Location",
                            enabled = !locked,
                            itemToString = { it.name },
                            onItemSelected = {
                                if (starting.value.location != it.id) {
                                    changeLocation(it.id)
                                }
                            },
                            modifier = Modifier.fillMaxWidth(0.8f),
                            selectedIndex = locations.indexOfFirst { it.id == starting.value.location }
                        )
                        LargeDropDownFromList(
                            items = exercises,
                            label = "Exercise",
                            enabled = !locked,
                            itemToString = { it.name },
                            onItemSelected = { changeExercise(it.id) },
                            modifier = Modifier.fillMaxWidth(0.8f),
                            selectedIndex = exercises.indexOfFirst { it.id == starting.value.exercise }
                        )
                        LargeDropDownFromList(
                            items = equipment.value,
                            label = "Equipment",
                            enabled = !locked,
                            itemToString = { it.name },
                            onItemSelected = { changeEquipment(it.id) },
                            modifier = Modifier.fillMaxWidth(0.8f),
                            selectedIndex = equipment.value.indexOfFirst { it.id == starting.value.equipment }
                        )
                    }
                }
                Card() {
                    RepsAndWeightSelector(
                        starting,
                        validReps,
                        enabled = !locked,
                        changeReps,
                        validWeight,
                        changeWeight,
                    )
                }
                Card() {
                    IntensitySelector(changeIntensity, starting, enabled = !locked)
                }
                Card() {
                    Column(
                        modifier = Modifier.padding(10.dp)
                    ) {
                        TextField(
                            label = { Text("Comment") },
                            modifier = Modifier.fillMaxWidth(0.8f),
                            enabled = !locked,
                            value = starting.value.comment.value,
                            onValueChange = { changeComment(Comment(it)) },
                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                            keyboardActions = KeyboardActions(onDone = { keyboardController?.hide() })
                        )
                    }
                }
                LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    this.item() { Spacer(modifier = Modifier.width(25.dp)) }
                    this.items(previousSets.value) { set ->
                        PreviousSetButton(set.reps, set.weight, set.intensity) { gotoSet(set) }
                    }
                }
                Spacer(modifier = Modifier.fillMaxHeight(0.1f))
            }
        }
    }
}

@Composable
private fun IntensitySelector(
    changeIntensity: (Intensity) -> Unit,
    starting: State<WorkoutSet>,
    enabled: Boolean = true,
) {
    var slider by remember(key1 = starting.value.intensity) {
        mutableFloatStateOf(
            Intensity.toInt(starting.value.intensity).toFloat()
        )
    }
    Column {
        Spacer(modifier = Modifier.height(10.dp))
        Row {
            Spacer(modifier = Modifier.width(10.dp))
            Text("Intensity: ${starting.value.intensity}")
        }
        Slider(
            enabled = enabled,
            modifier = Modifier.fillMaxWidth(0.8f),
            colors = SliderDefaults.colors(
                thumbColor = intensityColor(starting.value.intensity),
                activeTrackColor = intensityColor(starting.value.intensity)
            ),
            value = slider,
            valueRange = 0f..4f,
            steps = 3,
            onValueChange = { f -> slider = f },
            onValueChangeFinished = {
                val result = Intensity.fromInt(slider.toInt())
                if (result != null) {
                    changeIntensity(result)
                }
                slider = slider.toInt().toFloat()
            }
        )
    }
}

@Composable
private fun RepsAndWeightSelector(
    starting: State<WorkoutSet>,
    validReps: MutableState<Boolean>,
    enabled: Boolean = true,
    changeReps: (Reps) -> Unit,
    validWeight: MutableState<Boolean>,
    changeWeight: (Weight) -> Unit
) {
    Column(
        modifier = Modifier.padding(10.dp)
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.fillMaxWidth(0.8f)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.weight(1f)
            ) {
                IntField(
                    label = "Reps",
                    enabled = enabled,
                    start = starting.value.reps.value,
                ) { it ->
                    validReps.value = it != null
                    if (it != null) {
                        changeReps(Reps(it))
                    }
                }
            }
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.weight(1f)
            ) {
                IntField(
                    label = "Weight",
                    start = starting.value.weight.value,
                    onChange = { it ->
                        validWeight.value = it != null
                        if (it != null) {
                            changeWeight(Weight(it))
                        }
                    },
                    enabled = enabled
                )
            }
        }
    }
}

@Preview(
    name = "Dark",
    uiMode = UI_MODE_NIGHT_YES,
    showBackground = true
)
@Preview(
    name = "Light",
    uiMode = UI_MODE_NIGHT_NO,
    showBackground = true
)
@Composable
private fun Preview() {
    val setTemplate =
        WorkoutSet(
            id = SetId("a"),
            exercise = ExerciseId("a"),
            equipment = EquipmentId("equipA"),
            location = LocationId("locationA"),
            reps = Reps(0),
            weight = Weight(0),
            time = Instant.now(),
            intensity = Intensity.Normal,
            comment = Comment("")
        )
    StrongGiraffeTheme {
        Page(
            locked = true,
            toggleSetLock = { },
            starting = remember { mutableStateOf(setTemplate) },
            submit = { },
            delete = { },
            changeLocation = { },
            changeExercise = { },
            changeEquipment = { },
            changeReps = { },
            changeWeight = { },
            changeIntensity = { },
            changeComment = { },
            gotoSet = { },
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
            ),
            previousSets = remember {
                mutableStateOf(
                    listOf(setTemplate, setTemplate, setTemplate, setTemplate, setTemplate)
                )
            }
        )
    }
}