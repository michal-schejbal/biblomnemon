package com.ginoskos.biblomnemon.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import com.ginoskos.biblomnemon.ui.navigation.BottomNavigation
import com.ginoskos.biblomnemon.ui.navigation.TopNavigation
import com.ginoskos.biblomnemon.ui.theme.ThemeLayout

// TODO use KSP processor to generate a screen registry
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.SOURCE)
annotation class Screen

data class ScreenScaffoldHoist(
    val topBar: (@Composable (navController: NavHostController) -> Unit)? = { navController ->
        TopNavigation(navController)
    },
    val bottomBar: (@Composable (navController: NavHostController) -> Unit)? = { navController ->
        BottomNavigation(navController = navController)
    },
    val fab: (@Composable (navController: NavHostController) -> Unit)? = null,
    val snackBar: (@Composable () -> Unit)? = null
)

interface IScreen {
    val identifier: Any
    val isNavigationBarsVisible: Boolean get() = true

    val hoist: ScreenScaffoldHoist? get() = null

    fun register(builder: NavGraphBuilder, navController: NavHostController)
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

@Composable
fun ScreenDetailWrapper(
    paddingValues: PaddingValues = ThemeLayout.offset,
    toolbar: @Composable () -> Unit,
    content: @Composable () -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
        toolbar()
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            content()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScreenToolbar(
    title: @Composable () -> Unit = {},
    onBack: () -> Unit = { },
    content: @Composable RowScope.() -> Unit = {}
) {
    TopAppBar(
        title = title,
        navigationIcon = {
            IconButton(onClick = onBack) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
            }
        },
        actions = content,
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color.Transparent,
            titleContentColor = MaterialTheme.colorScheme.onSurface,
        )
    )
}