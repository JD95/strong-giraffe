package org.wspcgir.strong_giraffe.destinations

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.ViewModel
import org.wspcgir.strong_giraffe.model.Location
import org.wspcgir.strong_giraffe.views.EditPageList

abstract class LocationListPageViewModel : ViewModel() {
    abstract val locations: List<Location>
    abstract fun newLocation()
    abstract fun gotoEditLocationPage(loc: Location)
}

@Composable
fun LocationListPage(view: LocationListPageViewModel) {
    EditPageList(
        title = "Locations",
        items = view.locations,
        gotoNewPage = view::newLocation,
        gotoEditPage = view::gotoEditLocationPage,
        sortBy = { x, y -> x.name.compareTo(y.name) }
    ) {
        Text(it.name)
    }
}

@Preview
@Composable
private fun Preview() {
   LocationListPage(view = object : LocationListPageViewModel() {
       override val locations: List<Location>
           get() = emptyList()

       override fun newLocation() {
       }

       override fun gotoEditLocationPage(loc: Location) {
       }

   })
}