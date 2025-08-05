package com.ginoskos.biblomnemon.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import kotlinx.coroutines.flow.collectLatest

/**
 * Navigate back in the NavController stack.
 * Returns true if back navigation was handled, false otherwise.
 */
fun NavController.navigateBack(): Boolean {
    return this.popBackStack()
}

/**
 * Pops back and returns a value to the previous back stack entry.
 *
 * @param key The key for the value in the savedStateHandle.
 * @param value The value to store and return.
 */
fun <T> NavController.returnResult(key: String, value: T) {
    previousBackStackEntry
        ?.savedStateHandle
        ?.set(key, value)
    navigateBack()
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

@Composable
fun currentScreenByRoute(
    navController: NavHostController
): String? {
    val backStackEntry by navController.currentBackStackEntryAsState()
    val route = backStackEntry?.destination?.route

    return route
}