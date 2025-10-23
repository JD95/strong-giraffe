package org.wspcgir.strong_giraffe.destinations.edit_set

import android.annotation.SuppressLint
import android.os.Parcelable
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.toRoute
import kotlinx.serialization.Serializable
import org.wspcgir.strong_giraffe.destinations.EditExercise
import org.wspcgir.strong_giraffe.destinations.EditVariation
import org.wspcgir.strong_giraffe.model.Exercise
import org.wspcgir.strong_giraffe.model.ExerciseVariation
import org.wspcgir.strong_giraffe.repository.AppRepository
import org.wspcgir.strong_giraffe.views.SelectionPage
import kotlin.reflect.KType


@Serializable object EditPage
@Serializable object SelectExercise
@Serializable object SelectVariation

fun NavGraphBuilder.editSetGraph(
    navController: NavController,
    repo: AppRepository,
    typeMap: Map<KType, NavType<out Parcelable>>
)  {
    navigation<EditSet>(startDestination = EditPage, typeMap = typeMap) {
        composable<EditPage>(typeMap = typeMap) {
            val parent = rememberParent(navController, it)
            val navArgs = parent.toRoute<EditSet>()
            val view = viewModel<EditSetPageViewModel>(parent)
            LaunchedEffect(Unit) {
                val set = repo.getSetFromId(navArgs.id)
                view.init(repo, navController, set, navArgs.locked)
            }
            EditSetPage(view)
        }
        composable<SelectExercise>() { entry ->
            val parent = rememberParent(navController, entry)
            val view = viewModel<EditSetPageViewModel>(parent)
            var exercises: List<Exercise> by remember { mutableStateOf(emptyList()) }
            LaunchedEffect(Unit) {
                exercises = repo.getExercises()
            }
            SelectionPage(
                items = exercises,
                displayName = { it.name },
                onSelect = { selection ->
                    when (val data = view.data.value) {
                        is EditSetPageViewModel.Data.Loaded -> {
                            data.changeExercise(selection.id)
                            navController.popBackStack()
                        }
                        else -> { }
                    }
                },
                onEdit = { exercise -> navController.navigate(EditExercise(id = exercise.id)) },
                goBack = { navController.popBackStack() }
            )
        }
        composable<SelectVariation> { entry ->
            val parent = rememberParent(navController, entry)
            val view = viewModel<EditSetPageViewModel>(parent)
            var variations: List<ExerciseVariation> by remember { mutableStateOf(emptyList()) }
            val data = view.data.value
            LaunchedEffect(data) {
                when (data) {
                    is EditSetPageViewModel.Data.Loaded -> {
                        variations = repo.getVariationsForExercise(data.inProgress.value.exercise)
                    }
                    else -> { }
                }
            }
            SelectionPage(
                items = variations,
                displayName = { it.name },
                onSelect = { variation ->
                    when (data) {
                        is EditSetPageViewModel.Data.Loaded -> {
                            data.changeVariation(variation.id)
                            navController.popBackStack()
                        }
                        else -> { }
                    }
                },
                onEdit = { variation -> navController.navigate(EditVariation(id = variation.id)) },
                goBack = { navController.popBackStack() }
            )
        }
    }
}

@Composable
private fun rememberParent(
    navController: NavController,
    entry: NavBackStackEntry
): NavBackStackEntry {
    return remember(entry) {
        navController.getBackStackEntry<EditSet>()
    }
}