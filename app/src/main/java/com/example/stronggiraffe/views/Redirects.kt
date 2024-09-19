package com.example.stronggiraffe.views

import androidx.compose.runtime.Composable
import com.example.stronggiraffe.destinations.*
import com.example.stronggiraffe.model.ids.LocationId
import com.example.stronggiraffe.model.ids.MuscleId
import com.example.stronggiraffe.repository.AppRepository
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch


@Composable
fun LocationEditRedirection(
    scope: CoroutineScope,
    repo: AppRepository,
    dest: DestinationsNavigator
) {
    RequiredDataRedirect(
        missing = "Location"
    ) {
        scope.launch {
            val new = repo.newLocation()
            dest.navigate(
                EditLocationPageDestination(
                    EditLocationPageNavArgs(new.name, new.id)
                )
            )
        }
    }
}

@Composable
fun EquipmentEditRedirection(
    location: LocationId,
    scope: CoroutineScope,
    repo: AppRepository,
    dest: DestinationsNavigator
) {
    RequiredDataRedirect(
        missing = "Equipment"
    ) {
        scope.launch {
            val new = repo.newEquipment(location)
            dest.navigate(
                EditEquipmentPageDestination(
                    EditEquipmentPageNavArgs(new.id, new.name, new.location)
                )
            )
        }
    }
}

@Composable
fun MuscleEditRedirection(
    scope: CoroutineScope,
    repo: AppRepository,
    dest: DestinationsNavigator
) {
    RequiredDataRedirect(
        missing = "Muscle"
    ) {
        scope.launch {
            val new = repo.newMuscle()
            dest.navigate(
                EditMusclePageDestination(
                    EditMusclePageNavArgs(new.id, new.name)
                )
            )
        }
    }
}

@Composable
fun ExerciseEditRedirection(
    muscle: MuscleId,
    scope: CoroutineScope,
    repo: AppRepository,
    dest: DestinationsNavigator
) {
    RequiredDataRedirect(
        missing = "Exercise"
    ) {
        scope.launch {
            val new = repo.newExercise(muscle)
            dest.navigate(
                EditExercisePageDestination(
                    EditExercisePageNavArgs(new.id, new.name, new.muscle)
                )
            )
        }
    }
}
