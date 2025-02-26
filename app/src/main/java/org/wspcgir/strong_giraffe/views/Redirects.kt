package org.wspcgir.strong_giraffe.views

import androidx.compose.runtime.Composable
import org.wspcgir.strong_giraffe.destinations.*
import org.wspcgir.strong_giraffe.model.ids.MuscleId
import org.wspcgir.strong_giraffe.repository.AppRepository
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch


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
                EditExercisePageDestination(new.id)
            )
        }
    }
}
