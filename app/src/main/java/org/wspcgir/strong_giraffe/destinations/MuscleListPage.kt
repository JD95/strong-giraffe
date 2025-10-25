package org.wspcgir.strong_giraffe.destinations

import android.content.res.Configuration
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import kotlinx.serialization.Serializable
import org.wspcgir.strong_giraffe.model.Muscle
import org.wspcgir.strong_giraffe.model.set.MuscleSetHistory
import org.wspcgir.strong_giraffe.model.ids.MuscleId
import org.wspcgir.strong_giraffe.ui.theme.Blue
import org.wspcgir.strong_giraffe.ui.theme.Green
import org.wspcgir.strong_giraffe.ui.theme.Grey
import org.wspcgir.strong_giraffe.ui.theme.Orange
import org.wspcgir.strong_giraffe.ui.theme.StrongGiraffeTheme
import org.wspcgir.strong_giraffe.views.ModalDrawerScaffold
import kotlin.math.max
import androidx.compose.foundation.lazy.items as lazyItems

@Serializable
object MuscleList

abstract class MuscleListPageViewModel : ViewModel() {
    abstract val musclesWithSetCounts: Map<MuscleId, MuscleSetHistory>
    abstract fun new()
    abstract fun goto(value: Muscle)
}

fun overloadIndicator(thisWeek: Int, lastWeek: Int): Color {
    return if (thisWeek < lastWeek) {
        if (thisWeek in 0 until lastWeek.div(2)) {
            Grey
        } else {
            Blue
        }
    } else if (thisWeek == lastWeek) {
        Green
    } else {
        Orange
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MuscleListPage(view: MuscleListPageViewModel) {
    val musclesWithSetCounts = view.musclesWithSetCounts.entries.toList()
    ModalDrawerScaffold(
        title = "Muscles",
        actionButton = {
            FloatingActionButton(
                onClick = {
                    view.new()
                }
            ) {
                Icon(Icons.Default.Add, contentDescription = "Create New")
            }
        },
        drawerContent = { },
    ) { innerPadding ->
        if (musclesWithSetCounts.isNotEmpty()) {
            val ordered =
                musclesWithSetCounts.sortedWith(comparator = { x, y ->
                    x.value.name.compareTo(y.value.name)
                })
            LazyColumn(modifier = Modifier.fillMaxWidth().padding(innerPadding)){
                lazyItems(ordered) { item ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp, vertical = 10.dp)
                            .clickable(onClick = {
                                view.goto(Muscle(item.key, item.value.name))
                            }),
                    ) {
                        Box(
                            contentAlignment = Alignment.Center
                        ) {
                            Row(
                                modifier = Modifier
                                    .padding(10.dp)
                                    .fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                val thisWeek = item.value.thisWeek
                                val lastWeek = item.value.lastWeek
                                val name = item.value.name
                                val percentComplete =
                                    if (thisWeek < lastWeek && lastWeek > 0) {
                                        thisWeek.toFloat().div(lastWeek.toFloat())
                                    } else if (thisWeek >= lastWeek || lastWeek == 0) {
                                        1.0f
                                    } else {
                                        0.0f
                                    }
                                Text(name, modifier = Modifier.weight(0.3f))
                                ProgressBar(thisWeek, lastWeek, percentComplete,
                                    modifier = Modifier.height(30.dp).weight(0.7f)
                                )
                            }
                        }
                    }
                }
            }
        } else {
            Text("There's nothing here yet")
        }
    }
}

@Composable
private fun ProgressBar(
    thisWeek: Int,
    lastWeek: Int,
    percentComplete: Float,
    modifier: Modifier = Modifier,
) {

    Box(
        modifier = modifier
            .clip(CircleShape)
    ) {
        val color = overloadIndicator(thisWeek, lastWeek)
        val rounding = 50.dp
        Row (modifier = Modifier.fillMaxWidth()) {
            Box(modifier = Modifier
                .weight(max(percentComplete, 0.001f))
                .background(color)
                .border(BorderStroke(15.dp, SolidColor(color)))
                .fillMaxHeight()
            )
            Box(modifier = Modifier
                .clip(RoundedCornerShape(topEnd = rounding, bottomEnd = rounding))
                .width(IntrinsicSize.Min)
            ) {
                Box(
                    modifier = Modifier
                        .background(color)
                        .fillMaxHeight()
                        .width(IntrinsicSize.Min),
                    contentAlignment = Alignment.CenterEnd
                ) {
                    Text(
                        text = "$thisWeek/$lastWeek",
                        fontSize = 15.sp,
                        modifier = Modifier
                            .padding(horizontal = 10.dp)
                            .wrapContentSize(unbounded = true),
                    )
                }
            }
            Spacer(modifier = Modifier.weight(max(1f - percentComplete, 0.001f)))
        }
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
        MuscleListPage(object : MuscleListPageViewModel() {
            override val musclesWithSetCounts: Map<MuscleId, MuscleSetHistory>
                get() =
                    mapOf(
                        MuscleId("a") to MuscleSetHistory("Quads", 0, 9),
                        MuscleId("b") to MuscleSetHistory("Hamstrings", 1, 9),
                        MuscleId("c") to MuscleSetHistory("Calves", 2, 9),
                        MuscleId("d") to MuscleSetHistory("Glutes", 3, 9),
                        MuscleId("e") to MuscleSetHistory("Chest", 4, 9),
                        MuscleId("g") to MuscleSetHistory("Front Delts", 5, 9),
                        MuscleId("h") to MuscleSetHistory("Side Delts", 6, 9),
                        MuscleId("i") to MuscleSetHistory("Rear Delts", 10, 9),
                        MuscleId("j") to MuscleSetHistory("Biceps", 8, 9),
                        MuscleId("l") to MuscleSetHistory("Lats", 9, 9),
                    )

            override fun new() {
            }

            override fun goto(value: Muscle) {
            }
        })
    }
}