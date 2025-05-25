package org.wspcgir.strong_giraffe

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
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
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewModelScope
import androidx.room.Room
import com.ramcosta.composedestinations.DestinationsNavHost
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootGraph
import com.ramcosta.composedestinations.manualcomposablecalls.composable
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.wspcgir.strong_giraffe.destinations.EditEquipmentPage
import org.wspcgir.strong_giraffe.destinations.EditEquipmentPageDestination
import org.wspcgir.strong_giraffe.destinations.EditEquipmentPageNavArgs
import org.wspcgir.strong_giraffe.destinations.EditEquipmentPageViewModel
import org.wspcgir.strong_giraffe.destinations.EditExercisePage
import org.wspcgir.strong_giraffe.destinations.EditExercisePageDestination
import org.wspcgir.strong_giraffe.destinations.EditExercisePageViewModelImpl
import org.wspcgir.strong_giraffe.destinations.EditLocationPage
import org.wspcgir.strong_giraffe.destinations.EditLocationPageDestination
import org.wspcgir.strong_giraffe.destinations.EditLocationPageNavArgs
import org.wspcgir.strong_giraffe.destinations.EditLocationPageViewModel
import org.wspcgir.strong_giraffe.destinations.EditMusclePage
import org.wspcgir.strong_giraffe.destinations.EditMusclePageDestination
import org.wspcgir.strong_giraffe.destinations.EditMusclePageNavArgs
import org.wspcgir.strong_giraffe.destinations.EditMusclePageViewModel
import org.wspcgir.strong_giraffe.destinations.EditSetPageDestination
import org.wspcgir.strong_giraffe.destinations.EquipmentListPage
import org.wspcgir.strong_giraffe.destinations.EquipmentListPageDestination
import org.wspcgir.strong_giraffe.destinations.EquipmentListPageViewModel
import org.wspcgir.strong_giraffe.destinations.ExerciseListPage
import org.wspcgir.strong_giraffe.destinations.ExerciseListPageDestination
import org.wspcgir.strong_giraffe.destinations.ExerciseListPageViewModel
import org.wspcgir.strong_giraffe.destinations.HomePageDestination
import org.wspcgir.strong_giraffe.destinations.LocationListPage
import org.wspcgir.strong_giraffe.destinations.LocationListPageDestination
import org.wspcgir.strong_giraffe.destinations.LocationListPageViewModel
import org.wspcgir.strong_giraffe.destinations.MuscleListPage
import org.wspcgir.strong_giraffe.destinations.MuscleListPageDestination
import org.wspcgir.strong_giraffe.destinations.MuscleListPageViewModel
import org.wspcgir.strong_giraffe.destinations.RegisterEditSetPage
import org.wspcgir.strong_giraffe.destinations.RegisterSetListPage
import org.wspcgir.strong_giraffe.destinations.SetListPageDestination
import org.wspcgir.strong_giraffe.model.Equipment
import org.wspcgir.strong_giraffe.model.Exercise
import org.wspcgir.strong_giraffe.model.Location
import org.wspcgir.strong_giraffe.model.Muscle
import org.wspcgir.strong_giraffe.model.MuscleSetHistory
import org.wspcgir.strong_giraffe.model.ids.LocationId
import org.wspcgir.strong_giraffe.model.ids.MuscleId
import org.wspcgir.strong_giraffe.repository.AppDatabase
import org.wspcgir.strong_giraffe.repository.AppRepository
import org.wspcgir.strong_giraffe.repository.MIGRATION_1_2
import org.wspcgir.strong_giraffe.ui.theme.StrongGiraffeTheme
import org.wspcgir.strong_giraffe.views.PAGE_TITLE_FONT_SIZE
import java.time.Instant


@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val repo = AppRepositoryModule.provideAppRepository(applicationContext)
        setContent {
            StrongGiraffeTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background
                ) {
                    MainComponent(repo)
                }
            }
        }
    }
}

@Composable
fun MainComponent(repo: AppRepository) {
    DestinationsNavHost(navGraph = NavGraphs.root)

//    {
//        composable(HomePageDestination) {
//            HomePage(
//                gotoLocationsList = {
//                    destinationsNavigator.navigate(LocationListPageDestination)
//                }, gotoMuscleList = {
//                    destinationsNavigator.navigate(MuscleListPageDestination)
//                }, gotoExerciseList = {
//                    destinationsNavigator.navigate(ExerciseListPageDestination)
//                }, gotoSetList = {
//                    destinationsNavigator.navigate(SetListPageDestination)
//                }
//            )
//        }
//        composable(LocationListPageDestination) {
//            var locations by remember { mutableStateOf(emptyList<Location>()) }
//            val scope = rememberCoroutineScope()
//            LaunchedEffect(locations) {
//                locations = repo.getLocations()
//            }
//            LocationListPage(object : LocationListPageViewModel() {
//                override val locations: List<Location>
//                    get() = locations
//
//                override fun newLocation() {
//                    scope.launch {
//                        val loc = repo.newLocation()
//                        destinationsNavigator.navigate(
//                            EditLocationPageDestination(
//                                EditLocationPageNavArgs(loc.name, loc.id)
//                            )
//                        )
//                    }
//                }
//
//                override fun gotoEditLocationPage(loc: Location) {
//                    destinationsNavigator.navigate(
//                        EditLocationPageDestination(
//                            EditLocationPageNavArgs(loc.name, loc.id)
//                        )
//                    )
//                }
//            })
//        }
//        composable(EditLocationPageDestination) {
//            val navArgs = this.navArgs
//            val scope = rememberCoroutineScope()
//            EditLocationPage(object : EditLocationPageViewModel() {
//                override val startingName: String
//                    get() = navArgs.startingName
//                override val submit: (String) -> Unit
//                    get() = { newName ->
//                        scope.launch { repo.updateLocation(navArgs.id, newName) }
//                        destinationsNavigator.popBackStack()
//                    }
//
//                override fun delete() {
//                    viewModelScope.launch {
//                        repo.deleteLocation(navArgs.id)
//                    }
//                    destinationsNavigator.popBackStack()
//                }
//            })
//        }
//        composable(MuscleListPageDestination) {
//            var muscles by remember { mutableStateOf(emptyMap<MuscleId, MuscleSetHistory>()) }
//            LaunchedEffect(muscles) {
//                muscles = repo.setsForMusclesInWeek(Instant.now()).setCounts
//            }
//            MuscleListPage(view = object : MuscleListPageViewModel() {
//                override val musclesWithSetCounts: Map<MuscleId, MuscleSetHistory>
//                    get() = muscles
//
//                override fun new() {
//                    viewModelScope.launch {
//                        val new = repo.newMuscle()
//                        destinationsNavigator.navigate(
//                            EditMusclePageDestination(
//                                EditMusclePageNavArgs(new.id, new.name)
//                            )
//                        )
//                    }
//                }
//
//                override fun goto(value: Muscle) {
//                    destinationsNavigator.navigate(
//                        EditMusclePageDestination(
//                            EditMusclePageNavArgs(value.id, value.name)
//                        )
//                    )
//                }
//            })
//        }
//        composable(EditMusclePageDestination) {
//            val navArgs = this.navArgs
//            EditMusclePage(view = object : EditMusclePageViewModel() {
//                override val startingName: String
//                    get() = navArgs.startingName
//
//                override fun submit(name: String) {
//                    viewModelScope.launch {
//                        repo.updateMuscle(navArgs.muscleId, name)
//                    }
//                    destinationsNavigator.popBackStack()
//                }
//
//                override fun delete() {
//                    viewModelScope.launch {
//                        repo.deleteMuscle(navArgs.muscleId)
//                    }
//                    destinationsNavigator.popBackStack()
//                }
//            })
//        }
//        composable(SetListPageDestination) {
//            RegisterSetListPage(repo, destinationsNavigator)
//        }
//        composable(EditSetPageDestination) {
//            RegisterEditSetPage(navArgs, repo, destinationsNavigator)
//        }
//    }
}

fun locationRedirect(
    scope: CoroutineScope, repo: AppRepository, destinationsNavigator: DestinationsNavigator
) {
    scope.launch {
        val new = repo.newLocation()
        destinationsNavigator.navigate(
            EditLocationPageDestination(new.name, new.id)
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
@Destination<RootGraph>(start = true)
fun HomePage(
    gotoLocationsList: () -> Unit,
    gotoMuscleList: () -> Unit,
    gotoExerciseList: () -> Unit,
    gotoSetList: () -> Unit,
) {
    Scaffold() { innerPadding ->

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
    HomePage({}, {}, {}, {})
}