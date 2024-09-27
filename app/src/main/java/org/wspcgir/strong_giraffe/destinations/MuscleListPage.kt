package org.wspcgir.strong_giraffe.destinations

import android.content.res.Configuration
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.ViewModel
import org.wspcgir.strong_giraffe.model.Muscle
import org.wspcgir.strong_giraffe.views.EditPageList
import com.ramcosta.composedestinations.annotation.Destination
import org.wspcgir.strong_giraffe.model.MuscleSetHistory
import org.wspcgir.strong_giraffe.model.ids.MuscleId
import org.wspcgir.strong_giraffe.ui.theme.Green
import org.wspcgir.strong_giraffe.ui.theme.Grey
import org.wspcgir.strong_giraffe.ui.theme.Red
import org.wspcgir.strong_giraffe.ui.theme.StrongGiraffeTheme


abstract class MuscleListPageViewModel: ViewModel() {
    abstract val musclesWithSetCounts: Map<MuscleId, MuscleSetHistory>
    abstract fun new()
    abstract fun goto(value: Muscle)
}

fun overloadIndicator(thisWeek: Int, lastWeek: Int): Color {
    return if (thisWeek < lastWeek) {
        Red
    } else if (thisWeek == lastWeek) {
        Grey
    } else {
        Green
    }
}

@Composable
@Destination
fun MuscleListPage(view: MuscleListPageViewModel) {
    EditPageList(
        title = "Muscles",
        items = view.musclesWithSetCounts.entries.toList(),
        gotoNewPage = view::new,
        gotoEditPage = { view.goto(Muscle(it.key, it.value.name)) },
        sortBy = { x,y -> x.value.name.compareTo(y.value.name) },
        rowRender = { onClick, inner, item ->
            Button(
                onClick = { onClick(item) },
                colors = ButtonDefaults.buttonColors(
                    containerColor = overloadIndicator(item.value.thisWeek, item.value.lastWeek)
                )
            ) {
                inner(item)
            }
        }
    ) {
        Text(String.format("${it.value.name} | ${it.value.thisWeek}"))
    }
}

@Preview(
    name = "Dark",
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES
)
@Preview(
    name = "Light",
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_NO
)
@Composable
private fun Preview() {
    StrongGiraffeTheme {
        MuscleListPage(object : MuscleListPageViewModel(){
            override val musclesWithSetCounts: Map<MuscleId, MuscleSetHistory>
                get() =
                    mapOf(
                        MuscleId("a") to MuscleSetHistory("Quads", 12, 12),
                        MuscleId("b") to MuscleSetHistory("Hamstrings", 5, 2),
                        MuscleId("c") to MuscleSetHistory("Calves", 0, 0),
                        MuscleId("d") to MuscleSetHistory("Glutes", 4, 4),
                        MuscleId("e") to MuscleSetHistory("Chest", 2, 3),
                        MuscleId("f") to MuscleSetHistory("Triceps", 10, 6),
                        MuscleId("g") to MuscleSetHistory("Front Delts", 4, 2),
                        MuscleId("h") to MuscleSetHistory("Side Delts", 3, 3),
                        MuscleId("i") to MuscleSetHistory("Rear Delts", 3, 1),
                        MuscleId("j") to MuscleSetHistory("Biceps", 15, 0),
                        MuscleId("k") to MuscleSetHistory("Traps", 9, 4),
                        MuscleId("l") to MuscleSetHistory("Lats", 10, 5),
                    )

            override fun new() {
            }

            override fun goto(value: Muscle) {
            }
        })
    }
}