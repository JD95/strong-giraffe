package org.wspcgir.strong_giraffe.views

import androidx.compose.ui.graphics.Color
import org.wspcgir.strong_giraffe.model.Intensity
import org.wspcgir.strong_giraffe.ui.theme.Blue
import org.wspcgir.strong_giraffe.ui.theme.Green
import org.wspcgir.strong_giraffe.ui.theme.Grey
import org.wspcgir.strong_giraffe.ui.theme.Orange
import org.wspcgir.strong_giraffe.ui.theme.Red

fun intensityColor(source: Intensity): Color {
    return when (source) {
        is Intensity.NoActivation -> Grey
        is Intensity.Easy -> Blue
        is Intensity.Normal -> Green
        is Intensity.EarlyFailure -> Orange
        is Intensity.Pain -> Red
    }
}