package org.wspcgir.strong_giraffe.destinations

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import org.wspcgir.strong_giraffe.model.Exercise
import org.wspcgir.strong_giraffe.model.Muscle
import org.wspcgir.strong_giraffe.model.ids.MuscleId
import org.wspcgir.strong_giraffe.views.EditPageList
import org.wspcgir.strong_giraffe.views.RequiredDataRedirect
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootGraph
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import kotlinx.coroutines.launch
import org.wspcgir.strong_giraffe.AppRepositoryModule
import org.wspcgir.strong_giraffe.model.ids.ExerciseId
import org.wspcgir.strong_giraffe.repository.AppRepository

abstract class ExerciseListPageViewModel : ViewModel() {
    abstract val exercises: State<List<Exercise>>
    abstract fun gotoNew()
    abstract fun goto(value: Exercise)
    abstract fun redirectToCreateMuscle()

    abstract val muscles: State<List<Muscle>>
}


class ExerciseListPageViewModelImpl(
    private val repo: AppRepository,
    private val nav: DestinationsNavigator
) : ExerciseListPageViewModel() {

    private val exercisesMut: MutableState<List<Exercise>> = mutableStateOf(emptyList())
    private val musclesMut: MutableState<List<Muscle>> = mutableStateOf(emptyList())

    override val exercises: State<List<Exercise>>
        get() = exercisesMut

    override val muscles: State<List<Muscle>>
        get() = musclesMut

    override fun gotoNew() {
        viewModelScope.launch {
            val new = repo.newExercise(muscles.value[0].id)
            nav.navigate(
                EditExercisePageDestination(new.id)
            )
        }
    }

    override fun goto(value: Exercise) {
        nav.navigate(
            EditExercisePageDestination(value.id)
        )
    }

    override fun redirectToCreateMuscle() {
        viewModelScope.launch {
            val new = repo.newMuscle()

            nav.navigate(
                EditMusclePageDestination(
                    EditMusclePageNavArgs(new.id, new.name)
                )
            )
        }
    }

}

@Composable
@Destination<RootGraph>
fun ExerciseListPage(view: ExerciseListPageViewModel = hiltViewModel()) {
    if (view.muscles.value.isEmpty()) {
        RequiredDataRedirect(missing = "Muscle") {
            view.redirectToCreateMuscle()
        }
    } else {
        EditPageList(
            title = "Exercise",
            items = view.exercises.value,
            gotoNewPage = view::gotoNew,
            gotoEditPage = view::goto,
            sortBy = { x, y -> x.name.compareTo(y.name) }
        ) {
            Text(it.name)
        }
    }
}

@Preview
@Composable
private fun Preview() {
    ExerciseListPage(object : ExerciseListPageViewModel() {
        override val exercises: State<List<Exercise>>
            get() = mutableStateOf(
                listOf(
                    Exercise(ExerciseId("b"), "Lat Pulldown", MuscleId("a")),
                    Exercise(ExerciseId("a"), "Bench Press", MuscleId("b")),
                )
            )

        override val muscles: State<List<Muscle>>
            get() = mutableStateOf(
                listOf(
                    Muscle(MuscleId("a"), "Lats"),
                    Muscle(MuscleId("b"), "Chest"),
                )
            )

        override fun gotoNew() {}

        override fun goto(value: Exercise) {}

        override fun redirectToCreateMuscle() {}
    })
}
