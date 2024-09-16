package com.example.stronggiraffe.views

import androidx.compose.ui.graphics.Color
import com.example.stronggiraffe.model.Intensity

fun intensityColor(source: Intensity): Color {
    return when (source) {
        is Intensity.NoActivation -> Color.Cyan
        is Intensity.Easy -> Color.Blue
        is Intensity.Normal -> Color.Green
        is Intensity.EarlyFailure -> Color.Yellow
        is Intensity.Pain -> Color.Red
    }
}