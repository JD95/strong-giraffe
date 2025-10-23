package org.wspcgir.strong_giraffe.destinations

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import org.wspcgir.strong_giraffe.model.ExerciseVariation
import org.wspcgir.strong_giraffe.model.ids.ExerciseId
import org.wspcgir.strong_giraffe.model.ids.ExerciseVariationId
import org.wspcgir.strong_giraffe.ui.theme.StrongGiraffeTheme
import org.wspcgir.strong_giraffe.views.ModalDrawerScaffold

abstract class EditVariationPageViewModel(val id: ExerciseId) : ViewModel() {
    abstract val variations: State<List<ExerciseVariation>>
}

data class EditVariation(val id: ExerciseVariationId)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditVariationpage(view: EditVariationPageViewModel = viewModel()) {
    ModalDrawerScaffold(
        title = "Edit Variations",
        actionButton = {},
        drawerContent = {}
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxWidth(0.8f)
            ,
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            LazyColumn() {
                items(view.variations.value) { variation ->
                    Text(variation.name)
                }
            }
        }
    }
}

@Composable
@Preview
private fun Preview() {
    val exercise = ExerciseId("someExercise")
    StrongGiraffeTheme {
        EditVariationpage(object : EditVariationPageViewModel(exercise) {
            override val variations: State<List<ExerciseVariation>>
                get() = mutableStateOf(listOf(
                    ExerciseVariation(
                        id = ExerciseVariationId("variationA"),
                        name = "Dumbbell",
                        exercise = exercise,
                        location = null
                    )
                ))
        })
    }
}