package com.example.stronggiraffe.destinations

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.ViewModel
import com.example.stronggiraffe.model.Equipment
import com.example.stronggiraffe.model.Location
import com.example.stronggiraffe.model.ids.LocationId
import com.example.stronggiraffe.views.EditPageList
import com.example.stronggiraffe.views.RequiredDataRedirect
import com.ramcosta.composedestinations.annotation.Destination

abstract class EquipmentListPageViewModel : ViewModel() {
    abstract val equipment: List<Equipment>
    abstract fun gotoNew()
    abstract fun goto(value: Equipment)
    abstract fun redirectToCreateLocation()

    abstract val locations: List<Location>
}

@Composable
@Destination
fun EquipmentListPage(view: EquipmentListPageViewModel) {
    if (view.locations.isEmpty()) {
        RequiredDataRedirect(missing = "Location") {
            view.redirectToCreateLocation()
        }
    } else {
        EditPageList(
            title = "Equipment",
            items = view.equipment,
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
    EquipmentListPage(object : EquipmentListPageViewModel() {
        override val equipment: List<Equipment>
            get() = emptyList()

        override val locations: List<Location>
            get() = listOf(Location(LocationId("a"), "24 Hour"))

        override fun gotoNew() {
        }

        override fun goto(value: Equipment) {
        }

        override fun redirectToCreateLocation() {
        }
    })
}