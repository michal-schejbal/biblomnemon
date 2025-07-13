package com.ginoskos.biblomnemon.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.ginoskos.biblomnemon.ui.theme.BeigeBackground


internal object CardComponentDefaults {
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
        modifier = modifier
            .background(bg),
        colors = CardDefaults.cardColors(containerColor = bg)
    ) {
        content()
    }
}