package org.wspcgir.strong_giraffe

import android.os.Bundle
import android.os.Parcelable
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import androidx.room.Room
import org.wspcgir.strong_giraffe.destinations.*
import org.wspcgir.strong_giraffe.model.*
import org.wspcgir.strong_giraffe.model.ids.*
import org.wspcgir.strong_giraffe.repository.AppDatabase
import org.wspcgir.strong_giraffe.repository.AppRepository
import org.wspcgir.strong_giraffe.ui.theme.StrongGiraffeTheme
import org.wspcgir.strong_giraffe.views.PAGE_TITLE_FONT_SIZE
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.wspcgir.strong_giraffe.repository.MIGRATION_1_2
import java.time.Instant
import java.util.Collections.emptyList
import kotlinx.serialization.*
import kotlinx.serialization.json.*
import kotlin.reflect.typeOf

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val db =
            Room.databaseBuilder(applicationContext, AppDatabase::class.java, "data.db")
                .addMigrations(MIGRATION_1_2)
                .build()
        val dao = db.dao()
        setContent {
            StrongGiraffeTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background
                ) {
                    MainComponent(repo = AppRepository(dao))
                }
            }
        }
    }
}


@Serializable
object Home

inline fun <reified T : Parcelable> parcelableType(
    isNullableAllowed: Boolean = false,
    json: Json = Json
) = object : NavType<T>(isNullableAllowed = isNullableAllowed) {
    override fun get(bundle: Bundle, key: String): T? {
       return bundle.getParcelable(key, T::class.java)
    }

    override fun parseValue(value: String): T {
        return json.decodeFromString(value)
    }

    override fun put(bundle: Bundle, key: String, value: T) {
        return bundle.putParcelable(key, value)
    }

    override fun serializeAsValue(value: T): String = Json.encodeToString(value)
}

@Composable
fun MainComponent(repo: AppRepository) {
    val navController = rememberNavController()
    val typeMap = mapOf(
        typeOf<SetId>() to parcelableType<SetId>(),
        typeOf<MuscleId>() to parcelableType<MuscleId>(),
        typeOf<LocationId>() to parcelableType<LocationId>(),
        typeOf<ExerciseId>() to parcelableType<ExerciseId>(),
        typeOf<EquipmentId>() to parcelableType<EquipmentId>(),
        typeOf<EditLocation>() to parcelableType<EditLocation>(),
        typeOf<EditSet>() to parcelableType<EditSet>(),
        typeOf<EditMuscle>() to parcelableType<EditMuscle>(),
        typeOf<EditExercise>() to parcelableType<EditExercise>(),
    )
    NavHost(navController = navController, startDestination = Home, typeMap = typeMap) {
        composable<Home> {
            var scope = rememberCoroutineScope()
            HomePage(
                gotoLocationsList = {
                    navController.navigate(LocationList)
                }, gotoMuscleList = {
                    navController.navigate(MuscleList)
                }, gotoExerciseList = {
                    navController.navigate(ExerciseList)
                }, gotoSetList = {
                    navController.navigate(SetList)
                }, createBackup = {
                    scope.launch {
                        val backup = repo.createBackup()
                    }
                },
                restoreFromBackup = {
                    scope.launch {
                        val backup = null
                        repo.restoreFromBackup(backup!!)
                    }
                }
            )
        }
        composable<LocationList> {
            var locations by remember { mutableStateOf(emptyList<Location>()) }
            val scope = rememberCoroutineScope()
            LaunchedEffect(locations) {
                locations = repo.getLocations()
            }
            LocationListPage(object : LocationListPageViewModel() {
                override val locations: List<Location>
                    get() = locations

                override fun newLocation() {
                    scope.launch {
                        val loc = repo.newLocation()
                        navController.navigate(EditLocation(loc.name, loc.id))
                    }
                }

                override fun gotoEditLocationPage(loc: Location) {
                    navController.navigate(EditLocation(loc.name, loc.id))
                }
            })
        }
        composable<EditLocation>(typeMap = typeMap) {
            val navArgs: EditLocation = it.toRoute()
            val scope = rememberCoroutineScope()
            EditLocationPage(object : EditLocationPageViewModel() {
                override val startingName: String
                    get() = navArgs.startingName
                override val submit: (String) -> Unit
                    get() = { newName ->
                        scope.launch { repo.updateLocation(navArgs.id, newName) }
                        navController.popBackStack()
                    }

                override fun delete() {
                    viewModelScope.launch {
                        repo.deleteLocation(navArgs.id)
                    }
                    navController.popBackStack()
                }
            })
        }
        composable<EquipmentList> {
            var locations by remember { mutableStateOf(emptyList<Location>()) }
            var equipment by remember { mutableStateOf(emptyList<Equipment>()) }
            LaunchedEffect(equipment) {
                locations = repo.getLocations()
                equipment = repo.getEquipment()
            }
            EquipmentListPage(view = object : EquipmentListPageViewModel() {
                override val equipment: List<Equipment>
                    get() = equipment

                override val locations: List<Location>
                    get() = locations

                override fun gotoNew() {
                    viewModelScope.launch {
                        val new = repo.newEquipment(locations[0].id)
                        navController.navigate(
                            EditEquipment(
                                new.id,
                                new.name,
                                new.location
                            )
                        )
                    }
                }

                override fun goto(value: Equipment) {
                    navController.navigate(
                            EditEquipment(value.id, value.name, value.location)
                    )
                }

                override fun redirectToCreateLocation() {
                    locationRedirect(viewModelScope, repo, navController)
                }
            })
        }
        composable<EditEquipment>(typeMap = typeMap) {
            val navArgs: EditEquipment = it.toRoute()
            var locations by remember { mutableStateOf(emptyList<Location>()) }
            var equipment by remember { mutableStateOf(emptyList<Equipment>()) }
            LaunchedEffect(equipment) {
                locations = repo.getLocations()
                equipment = repo.getEquipment()
            }
            EditEquipmentPage(view = object : EditEquipmentPageViewModel() {
                override val startingName: String
                    get() = navArgs.name

                override val startingLocation: Location
                    get() = locations.first { it.id == navArgs.location }

                override val locations: List<Location>
                    get() = locations

                override fun submit(name: String, location: LocationId) {
                    viewModelScope.launch {
                        repo.updateEquipment(navArgs.id, name, location)
                    }
                    navController.popBackStack()
                }

                override fun redirectToCreateLocation() {
                    locationRedirect(viewModelScope, repo, navController)
                }

                override fun delete() {
                    viewModelScope.launch {
                        repo.deleteEquipment(navArgs.id)
                    }
                    navController.popBackStack()
                }
            })
        }
        composable<MuscleList> {
            var muscles by remember { mutableStateOf(emptyMap<MuscleId, MuscleSetHistory>()) }
            LaunchedEffect(muscles) {
                muscles = repo.setsForMusclesInWeek(Instant.now()).setCounts
            }
            MuscleListPage(view = object : MuscleListPageViewModel() {
                override val musclesWithSetCounts: Map<MuscleId, MuscleSetHistory>
                    get() = muscles

                override fun new() {
                    viewModelScope.launch {
                        val new = repo.newMuscle()
                        navController.navigate(EditMuscle(new.id, new.name))
                    }
                }

                override fun goto(value: Muscle) {
                    navController.navigate(EditMuscle(value.id, value.name))
                }
            })
        }
        composable<EditMuscle>(typeMap = typeMap) {
            val navArgs: EditMuscle = it.toRoute()
            EditMusclePage(view = object : EditMusclePageViewModel() {
                override val startingName: String
                    get() = navArgs.startingName

                override fun submit(name: String) {
                    viewModelScope.launch {
                        repo.updateMuscle(navArgs.muscleId, name)
                    }
                    navController.popBackStack()
                }

                override fun delete() {
                    viewModelScope.launch {
                        repo.deleteMuscle(navArgs.muscleId)
                    }
                    navController.popBackStack()
                }
            })
        }
        composable<ExerciseList> {
            var exercises by remember { mutableStateOf(emptyList<Exercise>()) }
            var muscles by remember { mutableStateOf(emptyList<Muscle>()) }
            LaunchedEffect(exercises, muscles) {
                exercises = repo.getExercises()
                muscles = repo.getMuscles()
            }
            ExerciseListPage(view = object : ExerciseListPageViewModel() {
                override val exercises: List<Exercise>
                    get() = exercises

                override fun gotoNew() {
                    viewModelScope.launch {
                        val new = repo.newExercise(muscles[0].id)
                        navController.navigate(EditExercise(new.id))
                    }
                }

                override fun goto(value: Exercise) {
                    navController.navigate(EditExercise(value.id))
                }

                override fun redirectToCreateMuscle() {
                    viewModelScope.launch {
                        val new = repo.newMuscle()

                        navController.navigate(EditMuscle(new.id, new.name))
                    }
                }

                override val muscles: List<Muscle>
                    get() = muscles
            })
        }
        composable<EditExercise>(typeMap = typeMap) {
            val navArgs: EditExercise = it.toRoute()
            EditExercisePage(
                view = EditExercisePageViewModelImpl(navArgs.id, repo, navController)
            )
        }
        composable<SetList> {
            RegisterSetListPage(repo, navController)
        }
        composable<EditSet>(typeMap = typeMap) {
            val navArgs: EditSet = it.toRoute()
            RegisterEditSetPage(navArgs, repo, navController)
        }
    }
}

fun locationRedirect(
    scope: CoroutineScope, repo: AppRepository, navController: NavController
) {
    scope.launch {
        val new = repo.newLocation()
        navController.navigate(EditLocation(new.name, new.id))
    }
}

@Composable
fun HomePageTopBar(
    backupData: () -> Unit,
    restoreFromBackup: () -> Unit
) {
    BottomAppBar() {
        Button(onClick = backupData) {
            Text("Backup")
        }
        Spacer(modifier = Modifier.width(5.dp))
        Button(onClick = restoreFromBackup) {
            Text("Restore")
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomePage(
    gotoLocationsList: () -> Unit,
    gotoMuscleList: () -> Unit,
    gotoExerciseList: () -> Unit,
    gotoSetList: () -> Unit,
    createBackup: () -> Unit,
    restoreFromBackup: () -> Unit
) {
    Scaffold(
        bottomBar = { HomePageTopBar(createBackup, restoreFromBackup) }
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {


            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.2f),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text("Strong", fontSize = PAGE_TITLE_FONT_SIZE)
                Text("Giraffe", fontSize = PAGE_TITLE_FONT_SIZE)
            }
            Spacer(modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.2f))
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.3f),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                val space = 10.dp
                Row() {
                    Button(onClick = gotoLocationsList) {
                        Text(text = "Locations")
                    }
                }
                Spacer(modifier = Modifier.height(space))
                Row() {
                    Button(onClick = gotoMuscleList) {
                        Text(text = "Muscles")
                    }
                    Spacer(modifier = Modifier.width(space))
                    Button(onClick = gotoExerciseList) {
                        Text(text = "Exercises")
                    }
                }
                Spacer(modifier = Modifier.height(space))
                Button(onClick = gotoSetList) {
                    Text(text = "Sets")
                }
            }
        }
    }
}

@Preview
@Composable
fun HomePagePreview() {
    HomePage({}, {}, {}, {}, {}, {})
}