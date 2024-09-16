package com.example.stronggiraffe.destinations

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.ViewModel
import com.example.stronggiraffe.model.Equipment
import com.example.stronggiraffe.views.EditPageList
import com.ramcosta.composedestinations.annotation.Destination

abstract class EquipmentListPageViewModel : ViewModel() {
    abstract val equipment: List<Equipment>
    abstract fun new()
    abstract fun goto(value: Equipment)
}

@Composable
@Destination
fun EquipmentListPage(view: EquipmentListPageViewModel) {
    EditPageList(
        items = view.equipment,
        gotoNewPage = view::new,
        gotoEditPage = view::goto
    ) {
        Text(it.name)
    }
}

@Preview
@Composable
private fun Preview() {
    EquipmentListPage(object : EquipmentListPageViewModel() {
        override val equipment: List<Equipment>
            get() = emptyList()

        override fun new() {
        }

        override fun goto(value: Equipment) {
        }
    })
}