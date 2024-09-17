package com.example.stronggiraffe

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewModelScope
import androidx.room.Room
import com.example.stronggiraffe.destinations.*
import com.example.stronggiraffe.model.*
import com.example.stronggiraffe.model.ids.*
import com.example.stronggiraffe.repository.AppDatabase
import com.example.stronggiraffe.repository.AppRepository
import com.example.stronggiraffe.ui.theme.StrongGiraffeTheme
import com.example.stronggiraffe.views.PAGE_TITLE_FONTSIZE
import com.ramcosta.composedestinations.DestinationsNavHost
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.manualcomposablecalls.composable
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val db = Room.databaseBuilder(applicationContext, AppDatabase::class.java, "data.db")
            .build()
        val dao = db.dao()
        setContent {
            StrongGiraffeTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MainComponent(repo = AppRepository(dao))
                }
            }
        }
    }
}

@Composable
fun MainComponent(repo: AppRepository) {
    DestinationsNavHost(navGraph = NavGraphs.root) {
        composable(HomePageDestination) {
            HomePage(
                gotoLocationsList = {
                    destinationsNavigator.navigate(LocationListPageDestination)
                },
                gotoEquipmentList = {
                    destinationsNavigator.navigate(EquipmentListPageDestination)
                }
            )
        }
        composable(LocationListPageDestination) {
            var locations by remember { mutableStateOf(emptyList<Location>()) }
            val scope = rememberCoroutineScope()
            LaunchedEffect(locations) {
                locations = repo.getLocations()
            }
            LocationListPage(
                object : LocationListPageViewModel() {
                    override val locations: List<Location>
                        get() = locations

                    override fun newLocation() {
                        scope.launch {
                            val loc = repo.newLocation()
                            destinationsNavigator.navigate(
                                EditLocationPageDestination(
                                    EditLocationPageNavArgs(loc.name, loc.id)
                                )
                            )
                        }
                    }

                    override fun gotoEditLocationPage(loc: Location) {
                        destinationsNavigator.navigate(
                            EditLocationPageDestination(
                                EditLocationPageNavArgs(loc.name, loc.id)
                            )
                        )
                    }
                }
            )
        }
        composable(EditLocationPageDestination) {
            val navArgs = this.navArgs
            val scope = rememberCoroutineScope()
            EditLocationPage(object : EditLocationPageViewModel() {
                override val startingName: String
                    get() = navArgs.startingName
                override val submit: (String) -> Unit
                    get() = { newName ->
                        scope.launch { repo.updateLocation(navArgs.id, newName) }
                        destinationsNavigator.popBackStack()
                    }
            })
        }
        composable(EquipmentListPageDestination) {
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
                        val new = repo.newEquipment("New Equipment", locations[0].id)
                        destinationsNavigator.navigate(
                            EditEquipmentPageDestination(
                                EditEquipmentPageNavArgs(new.id, new.name, new.location)
                            )
                        )
                    }
                }

                override fun goto(value: Equipment) {
                    destinationsNavigator.navigate(
                        EditEquipmentPageDestination(
                            EditEquipmentPageNavArgs(value.id, value.name, value.location)
                        )
                    )
                }

                override fun redirectToCreateLocation() {
                    locationRedirect(viewModelScope, repo, destinationsNavigator)
                }
            })
        }
        composable(EditEquipmentPageDestination) {
            val navArgs = this.navArgs
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
                    destinationsNavigator.popBackStack()
                }

                override fun redirectToCreateLocation() {
                   locationRedirect(viewModelScope, repo, destinationsNavigator)
                }
            })
        }
    }
}

fun locationRedirect(
    scope: CoroutineScope,
    repo: AppRepository,
    destinationsNavigator: DestinationsNavigator
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
@Destination(start = true)
fun HomePage(
    gotoLocationsList: () -> Unit,
    gotoEquipmentList: () -> Unit,
) {
    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { /* create new set */ }
            ) {
                Icon(Icons.Default.Add, contentDescription = "Record New Set")
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
            Text("Strong", fontSize = PAGE_TITLE_FONTSIZE)
            Text("Giraffe", fontSize = PAGE_TITLE_FONTSIZE)
            Spacer(modifier = Modifier.fillMaxHeight(0.2f))
            Button(onClick = gotoLocationsList) {
                Text(text = "Locations")
            }
            Button(onClick = gotoEquipmentList) {
                Text(text = "Equipment")
            }
            Button(onClick = { }) {
                Text(text = "Muscles")
            }
            Button(onClick = { }) {
                Text(text = "Exercises")
            }
            Button(onClick = { }) {
                Text(text = "Sets")
            }
        }
    }
}