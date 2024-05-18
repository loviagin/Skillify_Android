package com.lovigin.app.skillify.activity.element

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun HomeText (text: String) {
    Text(
        text = text,
        modifier = Modifier.padding(horizontal = 18.dp),
        fontSize = 18.sp,
        color = Color.Black.copy(0.8f)
    )
}