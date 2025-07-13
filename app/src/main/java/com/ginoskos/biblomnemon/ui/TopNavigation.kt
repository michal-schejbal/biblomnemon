package com.ginoskos.biblomnemon.ui

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ginoskos.biblomnemon.ui.components.CircleIconComponent
import com.ginoskos.biblomnemon.ui.theme.BiblomnemonTheme
import com.ginoskos.biblomnemon.ui.theme.Surface

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopNavigation(
    modifier: Modifier = Modifier,
    title: String,
    onProfileClick: () -> Unit = {},
    onAddClick: () -> Unit = {},
    onSearchClick: () -> Unit = {}
) {
    TopAppBar(
        title = {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge
            )
        },
        actions = {
            IconButton(onClick = onSearchClick) {
                CircleIconComponent(
                    icon = Icons.Default.Search,
                    background = Surface,
                    size = 35.dp,
                    contentDescription = "Search"
                )
            }
            IconButton(onClick = onAddClick) {
                CircleIconComponent(
                    icon = Icons.Default.Add,
                    background = Surface,
                    size = 35.dp,
                    contentDescription = "Add"
                )
            }
            IconButton(onClick = onProfileClick) {
                CircleIconComponent(
                    icon = Icons.Default.AccountCircle,
                    background = Surface,
                    size = 35.dp,
                    contentDescription = "Profile"
                )
            }
        },
        modifier = modifier,
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color.Transparent,
            titleContentColor = MaterialTheme.colorScheme.onSurface,
        )
    )
}

@Preview
@Composable
private fun TopNavigationPreview() {
    BiblomnemonTheme {
        TopNavigation(title = "Biblomnēmon")
    }
}
