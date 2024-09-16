package com.example.stronggiraffe.destinations

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.lifecycle.ViewModel
import com.example.stronggiraffe.model.Location
import com.example.stronggiraffe.views.EditPageList
import com.ramcosta.composedestinations.annotation.Destination

abstract class LocationListPageViewModel : ViewModel() {
    abstract val locations: List<Location>
    abstract fun newLocation()
    abstract fun gotoEditLocationPage(loc: Location)
}

@Composable
@Destination
fun LocationListPage(view: LocationListPageViewModel) {
    EditPageList(
        items = view.locations,
        gotoNewPage = view::newLocation,
        gotoEditPage = view::gotoEditLocationPage
    ) {
        Text(it.name)
    }
}