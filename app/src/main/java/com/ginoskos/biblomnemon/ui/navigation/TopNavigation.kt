package com.ginoskos.biblomnemon.ui.navigation

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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.ginoskos.biblomnemon.R
import com.ginoskos.biblomnemon.ui.screens.search.SearchScreen
import com.ginoskos.biblomnemon.ui.theme.BiblomnemonTheme
import com.ginoskos.biblomnemon.ui.theme.Surface
import com.ginoskos.biblomnemon.ui.theme.components.CircleIconComponent

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopNavigation(
    navController: NavHostController,
    title: String = stringResource(id = R.string.app_name)
) {
    TopAppBar(
        title = {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge
            )
        },
        actions = {
            IconButton(onClick = {
                navController.navigate(SearchScreen.Identifier)
            }) {
                CircleIconComponent(
                    icon = Icons.Default.Search,
                    background = Surface,
                    size = 35.dp,
                    contentDescription = "Search"
                )
            }
            IconButton(onClick = { }) {
                CircleIconComponent(
                    icon = Icons.Default.Add,
                    background = Surface,
                    size = 35.dp,
                    contentDescription = "Add"
                )
            }
            IconButton(onClick = { }) {
                CircleIconComponent(
                    icon = Icons.Default.AccountCircle,
                    background = Surface,
                    size = 35.dp,
                    contentDescription = "Profile"
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color.Transparent,
            titleContentColor = MaterialTheme.colorScheme.onSurface,
        )
    )
}

@Preview
@Composable
private fun TopNavigationPreview() {
    val navController = rememberNavController()
    BiblomnemonTheme {
        TopNavigation(navController = navController, title = stringResource(id = R.string.app_name))
    }
}
