package com.ginoskos.biblomnemon.ui.components

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ginoskos.biblomnemon.ui.theme.BeigeBackground


private object CardComponentDefaults {
    @Composable
    fun background(): Color =
        if (isSystemInDarkTheme()) {
            BeigeBackground.Dark.color
        } else {
            BeigeBackground.Light.color
        }
}

@Composable
fun CardComponent(
    modifier: Modifier,
    background: Color? = null,
    content: @Composable () -> Unit
) {
    val bg = background ?: CardComponentDefaults.background()

    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = bg)
    ) {
        content()
    }
}

@Preview(showBackground = true)
@Composable
fun CardComponentPreview() {
    CardComponent(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth()
    ) {
        Text(
            text = "CardComponent",
            modifier = Modifier.padding(16.dp)
        )
    }
}