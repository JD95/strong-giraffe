package org.wspcgir.strong_giraffe.views

import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import org.wspcgir.strong_giraffe.destinations.*
import org.wspcgir.strong_giraffe.model.ids.LocationId
import org.wspcgir.strong_giraffe.model.ids.MuscleId
import org.wspcgir.strong_giraffe.repository.AppRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch


@Composable
fun LocationEditRedirection(
    scope: CoroutineScope,
    repo: AppRepository,
    dest: NavController
) {
    RequiredDataRedirect(
        missing = "Location"
    ) {
        scope.launch {
            val new = repo.newLocation()
            dest.navigate(EditLocation(new.name, new.id))
        }
    }
}

@Composable
fun EquipmentEditRedirection(
    location: LocationId,
    scope: CoroutineScope,
    repo: AppRepository,
    dest: NavController
) {
    RequiredDataRedirect(
        missing = "Equipment"
    ) {
        scope.launch {
            val new = repo.newEquipment(location)
            dest.navigate(EditEquipment(new.id, new.name, new.location))
        }
    }
}

@Composable
fun MuscleEditRedirection(
    scope: CoroutineScope,
    repo: AppRepository,
    dest: NavController
) {
    RequiredDataRedirect(
        missing = "Muscle"
    ) {
        scope.launch {
            val new = repo.newMuscle()
            dest.navigate(EditMuscle(new.id, new.name))
        }
    }
}

@Composable
fun ExerciseEditRedirection(
    muscle: MuscleId,
    scope: CoroutineScope,
    repo: AppRepository,
    dest: NavController
) {
    RequiredDataRedirect(
        missing = "Exercise"
    ) {
        scope.launch {
            val new = repo.newExercise(muscle)
            dest.navigate(EditExercise(new.id))
        }
    }
}
