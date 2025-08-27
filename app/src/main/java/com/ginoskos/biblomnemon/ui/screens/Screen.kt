package com.ginoskos.biblomnemon.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.ginoskos.biblomnemon.R
import com.ginoskos.biblomnemon.ui.navigation.BottomNavigation
import com.ginoskos.biblomnemon.ui.navigation.TopNavigation
import com.ginoskos.biblomnemon.ui.theme.ThemeLayout

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppScreen(
    containerColor: Color = MaterialTheme.colorScheme.background,
    topBar: @Composable () -> Unit = {},
    bottomBar: @Composable () -> Unit = {},
    fab: @Composable () -> Unit = {},
    snackBar: @Composable () -> Unit = {},
    content: @Composable (padding: PaddingValues) -> Unit
) {
    Scaffold(
        topBar = topBar,
        bottomBar = bottomBar,
        floatingActionButton = fab,
        snackbarHost = snackBar,
        containerColor = containerColor,
        contentColor = MaterialTheme.colorScheme.onBackground,
    ) { padding ->
        content(padding)
    }
}


@Composable
fun MainScreen(
    navController: NavHostController,
    title: String? = null,
    fab: @Composable () -> Unit = {},
    snackBar: @Composable () -> Unit = {},
    content: @Composable () -> Unit
) {
    AppScreen(
        topBar = {
            TopNavigation(
                navController = navController,
                title = title ?: stringResource(id = R.string.app_name)
            )
        },
        bottomBar = {
            BottomNavigation(navController)
        },
        fab = fab,
        snackBar = snackBar,
        content = { padding ->
            ScreenSurface(padding) {
                ScreenWrapper {
                    content()
                }
            }
        }
    )
}

@Composable
fun SubScreen(
    navController: NavHostController,
    topBar: @Composable () -> Unit = {},
    snackBar: @Composable () -> Unit = {},
    fab: @Composable () -> Unit = {},
    content: @Composable () -> Unit
) {
    AppScreen(
        topBar = topBar,
        snackBar = snackBar,
        fab = fab,
        content = { padding ->
            ScreenSurface(padding) {
                ScreenWrapper {
                    content()
                }
            }
        }
    )
}

@Composable
fun ScreenSurface(
    innerPadding: PaddingValues,
    content: @Composable () -> Unit
) {
    Surface(
        modifier = Modifier.Companion
            .padding(innerPadding)
            .fillMaxSize(),
//                        tonalElevation = 1.dp,
        shadowElevation = 10.dp,
        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)
    ) {
        content()
    }
}

@Composable
fun ScreenWrapper(
    paddingValues: PaddingValues = ThemeLayout.offset,
    content: @Composable () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
    ) {
        content()
    }
}