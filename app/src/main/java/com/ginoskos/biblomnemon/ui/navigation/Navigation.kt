package com.ginoskos.biblomnemon.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.currentBackStackEntryAsState
import com.ginoskos.biblomnemon.core.app.NavigationItems
import com.ginoskos.biblomnemon.ui.screens.IScreen
import com.ginoskos.biblomnemon.ui.screens.home.HomeScreen
import com.ginoskos.biblomnemon.ui.screens.scanner.ScanScreen
import com.ginoskos.biblomnemon.ui.screens.search.SearchDetailScreen
import kotlinx.coroutines.flow.collectLatest

private val screens = NavigationItems.entries.map { it.screen } + listOf(
    SearchDetailScreen,
    ScanScreen
)

@Composable
fun NavigationGraph(modifier: Modifier = Modifier, navController: NavHostController, setTopBar: (title: String) -> Unit) {
    NavHost(navController = navController, startDestination = HomeScreen.Identifier) {
        screens.forEach { screen ->
            screen.register(this, navController)
        }
    }
}

@Composable
fun currentScreenByRoute(
    navController: NavHostController
): IScreen? {
    val backStackEntry by navController.currentBackStackEntryAsState()
    val route = backStackEntry?.destination?.route

    if (route == null) return null
    return screens.firstOrNull { screen ->
        route.contains(screen::class.simpleName ?: "")
    }
}

/**
 * Observe a navigation result directly inside a Composable.
 *
 * This automatically ties collection to the Composable's lifecycle.
 *
 * Once consumed, it automatically clears the value in SavedStateHandle
 * to avoid re-triggering on recomposition or process recreation.
 */
@Composable
inline fun <reified T> NavController.ObserveResult(
    key: String,
    crossinline onResult: (T) -> Unit
) {
    val backStackEntry = currentBackStackEntry
    LaunchedEffect(backStackEntry) {
        backStackEntry?.savedStateHandle
            ?.getStateFlow<T?>(key, null)
            ?.collectLatest { value ->
                if (value != null) {
                    onResult(value)
                    backStackEntry.savedStateHandle.set<T?>(key, null)
                }
            }
    }
}