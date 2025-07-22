package com.ginoskos.biblomnemon.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ginoskos.biblomnemon.ui.theme.BiblomnemonTheme

@Composable
fun MessageComponent(
    modifier: Modifier = Modifier,
    message: String,
    icon: ImageVector? = null,
    color: Color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
) {
    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (icon != null) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = color
            )

            Spacer(modifier = Modifier.height(8.dp))
        }

        Text(
            text = message,
            style = MaterialTheme.typography.bodyMedium,
            color = color
        )
    }
}

@Preview(showBackground = true, name = "With Icon")
@Composable
fun MessageComponentWithIconPreview() {
    BiblomnemonTheme {
        MessageComponent(
            message = "Start typing to search for books",
            icon = Icons.Default.Search,
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Preview(showBackground = true, name = "Without Icon")
@Composable
fun MessageComponentWithoutIconPreview() {
    BiblomnemonTheme {
        MessageComponent(
            message = "No results found",
            modifier = Modifier.padding(16.dp)
        )
    }
}