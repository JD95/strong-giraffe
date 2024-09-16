package com.example.stronggiraffe.destinations

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.example.stronggiraffe.views.intensityColor
import com.example.stronggiraffe.model.SetSummary
import com.example.stronggiraffe.model.WorkoutSet
import com.example.stronggiraffe.model.ids.SetId
import com.ramcosta.composedestinations.annotation.Destination

@OptIn(ExperimentalMaterial3Api::class)
@Composable
@Destination
fun SetListPage(
    sets: List<SetSummary>,
    new: () -> WorkoutSet,
    goto: (SetId) -> Unit
) {
    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { goto(new().id) }
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
            sets.forEach { item ->
                Button(
                    colors = ButtonDefaults.buttonColors(
                        containerColor = intensityColor(item.intensity)
                    ),
                    onClick = { goto(item.id) }
                ) {
                    Text("${item.exercise} | ${item.weight} | ${item.reps}")
                }
            }
        }
    }


}