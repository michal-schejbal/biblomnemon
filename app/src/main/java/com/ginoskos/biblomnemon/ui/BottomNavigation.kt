package com.ginoskos.biblomnemon.ui

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ginoskos.biblomnemon.ui.theme.BiblomnemonTheme

/**
 * Represents a single destination that can be reached from the bottom navigation bar.
 */
sealed class BottomNavItem(val title: String, val icon: ImageVector) {
    object Home : BottomNavItem("Home", Icons.Default.Home)
    object Search : BottomNavItem("Search", Icons.Default.Search)
    object Profile : BottomNavItem("Profile", Icons.Default.AccountCircle)
}

private val items = listOf(
    BottomNavItem.Home,
    BottomNavItem.Search,
    BottomNavItem.Profile
)

@Composable
fun BottomNavigation(
    selected: BottomNavItem = BottomNavItem.Home,
    onSelect: (BottomNavItem) -> Unit = { }
) {
    Surface(
        shadowElevation = 8.dp // tweak to taste
    ) {
        NavigationBar(
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.onSurface
        ) {
            items.forEach { item ->
                val isSelected = item == selected
                NavigationBarItem(
                    selected = isSelected,
                    onClick = { onSelect(item) },
                    icon = {
                        Icon(
                            imageVector = item.icon,
                            contentDescription = item.title
                        )
                    },
                    label = { Text(item.title) }
                )
            }
        }
    }
}

@Preview
@Composable
private fun BottomNavigationBarPreview() {
    BiblomnemonTheme {
        BottomNavigation(BottomNavItem.Search)
    }
}