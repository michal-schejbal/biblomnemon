package com.ginoskos.biblomnemon.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.ginoskos.biblomnemon.ui.theme.BeigeBackground

private object CircleIconComponentDefaults {
    @Composable
    fun background(): Color =
        if (isSystemInDarkTheme()) {
            BeigeBackground.Dark.color
        } else {
            BeigeBackground.Light.color
        }
}

@Composable
fun CircleIconComponent(
    icon: ImageVector,
    background: Color? = null,
    tint: Color = MaterialTheme.colorScheme.primary,
    size: Dp = 40.dp,
    contentDescription: String? = null
) {
    val bg = background ?: CircleIconComponentDefaults.background()

    Box(
        modifier = Modifier
            .size(size)
            .clip(CircleShape)
            .background(bg),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            tint = tint
        )
    }
}