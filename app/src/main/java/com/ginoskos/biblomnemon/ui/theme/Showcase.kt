package com.ginoskos.biblomnemon.ui.theme

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ginoskos.biblomnemon.ui.components.CardComponent
import com.ginoskos.biblomnemon.ui.components.CircleIconComponent

@Composable
fun ThemeShowcase(darkTheme: Boolean) {
    BiblomnemonTheme(darkTheme = darkTheme) {
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(top = 25.dp),
            color = MaterialTheme.colorScheme.surface
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth()
            ) {
                Text(
                    "Your Reading Timeline",
                    style = MaterialTheme.typography.displayLarge,
                    color = MaterialTheme.colorScheme.onBackground
                )

                Text(
                    "Keep track of your journey through books.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onBackground
                )

                Button(onClick = {}) {
                    Text("Start Reading")
                }

                AssistChip(
                    onClick = {},
                    label = { Text("Vocabulary Progress") },
                    colors = AssistChipDefaults.assistChipColors(
                        containerColor = ChipBackground,
                        labelColor = Color.White
                    )
                )

                HorizontalDivider()

                CardComponent(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            "Recent Activity",
                            style = MaterialTheme.typography.titleLarge
                        )
                        Text(
                            "You read 14 pages from *The Republic*.",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }

                Text(
                    "Last synced 2 hours ago",
                    style = MaterialTheme.typography.labelSmall
                )

                var text by remember { mutableStateOf("") }
                OutlinedTextField(
                    value = text,
                    onValueChange = { text = it },
                    label = { Text("Search book...") },
                    modifier = Modifier.fillMaxWidth()
                )

                var notificationsEnabled by remember { mutableStateOf(true) }
                Row {
                    Text("Enable notifications", modifier = Modifier.weight(1f))
                    Switch(checked = notificationsEnabled, onCheckedChange = { notificationsEnabled = it })
                }

                var progress by remember { mutableFloatStateOf(0.5f) }
                Text("Reading Speed Preference")
                Slider(value = progress, onValueChange = { progress = it })

                Text("Daily Goal Progress")
                LinearProgressIndicator(progress = { progress })

                Text("Weekly Goal Progress")
                CircularProgressIndicator(
                    progress = { progress },
                    modifier = Modifier.padding(top = 4.dp),
                )

                Row(verticalAlignment = Alignment.CenterVertically) {
                    CircleIconComponent(icon = Icons.Default.Favorite)
                    Text(
                        "Mark as Favorite",
                        modifier = Modifier.padding(start = 8.dp),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }
}


@Preview(name = "Light Mode", showSystemUi = true)
@Composable
fun LightPreview() {
    ThemeShowcase(darkTheme = false)
}

@Preview(name = "Dark Mode", showSystemUi = true)
@Composable
fun DarkPreview() {
    ThemeShowcase(darkTheme = true)
}