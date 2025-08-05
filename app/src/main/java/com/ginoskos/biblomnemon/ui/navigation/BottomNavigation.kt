package com.ginoskos.biblomnemon.ui.navigation

import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.ginoskos.biblomnemon.ui.theme.BiblomnemonTheme



@Composable
fun BottomNavigation(
    navController: NavHostController
) {
    val selected = currentScreenByRoute(navController) ?: BottomNavigationItems.Home

    Surface(shadowElevation = 8.dp) {
        NavigationBar(
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.onSurface
        ) {
            BottomNavigationItems.entries.forEach { item ->
                NavigationBarItem(
                    selected = item.route == selected,
                    onClick = {
                        navController.navigate(item.route)
                    },
                    icon = { item.iconRes?.let {
                        Icon(painter = painterResource(id = item.iconRes), contentDescription = stringResource(id = item.titleRes))
                    }},
                    label = { Text(stringResource(id = item.titleRes)) }
                )
            }
        }
    }
}

@Preview
@Composable
private fun BottomNavigationBarPreview() {
    val navController = rememberNavController()
    BiblomnemonTheme {
        BottomNavigation(navController)
    }
}