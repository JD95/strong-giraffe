package com.example.stronggiraffe.views

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp

@Composable
fun RequiredDataRedirect(
    missing: String,
    redirect: () -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.fillMaxHeight(0.3f))

        Text(
            text = "At least one $missing is required before proceeding",
            softWrap = true,
            fontSize = 30.sp,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.fillMaxHeight(0.3f))

        Button(onClick = redirect) {
            Text("Continue")
        }
    }
}

@Preview
@Composable
private fun Preview() {
   RequiredDataRedirect(
       missing = "Locations",
       redirect = { }
   )
}