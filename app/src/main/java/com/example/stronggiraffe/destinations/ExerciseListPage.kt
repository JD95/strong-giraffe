package com.example.stronggiraffe.destinations

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.ViewModel
import com.example.stronggiraffe.model.Exercise
import com.example.stronggiraffe.model.Muscle
import com.example.stronggiraffe.model.ids.MuscleId
import com.example.stronggiraffe.views.EditPageList
import com.example.stronggiraffe.views.RequiredDataRedirect
import com.ramcosta.composedestinations.annotation.Destination

abstract class ExerciseListPageViewModel : ViewModel() {
    abstract val exercises: List<Exercise>
    abstract fun gotoNew()
    abstract fun goto(value: Exercise)
    abstract fun redirectToCreateMuscle()

    abstract val muscles: List<Muscle>
}

@Composable
@Destination
fun ExerciseListPage(view: ExerciseListPageViewModel) {
    if (view.muscles.isEmpty()) {
        RequiredDataRedirect(missing = "Muscle") {
            view.redirectToCreateMuscle()
        }
    } else {
        EditPageList(
            title = "Exercise",
            items = view.exercises,
            gotoNewPage = view::gotoNew,
            gotoEditPage = view::goto
        ) {
            Text(it.name)
        }
    }
}

@Preview
@Composable
private fun Preview() {
    ExerciseListPage(object : ExerciseListPageViewModel() {
        override val exercises: List<Exercise>
            get() = emptyList()

        override val muscles: List<Muscle>
            get() = listOf(Muscle(MuscleId("a"), "Lats"))

        override fun gotoNew() {
        }

        override fun goto(value: Exercise) {
        }

        override fun redirectToCreateMuscle() {
        }
    })
}
