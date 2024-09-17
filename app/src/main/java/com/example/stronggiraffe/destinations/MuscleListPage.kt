package com.example.stronggiraffe.destinations

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.example.stronggiraffe.model.Muscle
import com.example.stronggiraffe.views.EditPageList
import com.ramcosta.composedestinations.annotation.Destination

@Composable
@Destination
fun MuscleListPage(
    muscles: List<Muscle>,
    new: () -> Unit,
    goto: (Muscle) -> Unit
) {
    EditPageList(
        title = "Muscles",
        items = muscles,
        gotoNewPage = new,
        gotoEditPage = goto
    ) {
        Text(it.name)
    }
}