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
import androidx.room.Room
import com.example.stronggiraffe.destinations.*
import com.example.stronggiraffe.model.*
import com.example.stronggiraffe.model.ids.*
import com.example.stronggiraffe.repository.AppDatabase
import com.example.stronggiraffe.repository.AppRepository
import com.example.stronggiraffe.ui.theme.StrongGiraffeTheme
import com.ramcosta.composedestinations.DestinationsNavHost
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.manualcomposablecalls.composable
import kotlinx.coroutines.launch

val FIELD_NAME_FONT_SIZE = 30.sp
val PAGE_TITLE_FONTSIZE = 60.sp

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
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
@Destination(start = true)
fun HomePage(gotoLocationsList: () -> Unit) {
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
            Button(onClick = gotoLocationsList) {
                Text(text = "Locations")
            }
            Button(onClick = { }) {
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