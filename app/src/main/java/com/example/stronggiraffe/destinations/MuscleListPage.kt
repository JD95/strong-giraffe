package com.example.stronggiraffe.destinations

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.lifecycle.ViewModel
import com.example.stronggiraffe.model.Muscle
import com.example.stronggiraffe.views.EditPageList
import com.ramcosta.composedestinations.annotation.Destination


abstract class MuscleListPageViewModel: ViewModel() {

    abstract val muscles: List<Muscle>
    abstract fun new()
    abstract fun goto(value: Muscle)
}

@Composable
@Destination
fun MuscleListPage(view: MuscleListPageViewModel) {
    EditPageList(
        title = "Muscles",
        items = view.muscles,
        gotoNewPage = view::new,
        gotoEditPage = view::goto
    ) {
        Text(it.name)
    }
}