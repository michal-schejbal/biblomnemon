package com.ginoskos.biblomnemon.core.app

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import com.ginoskos.biblomnemon.R
import com.ginoskos.biblomnemon.ui.screens.IScreen
import com.ginoskos.biblomnemon.ui.screens.home.HomeScreen
import com.ginoskos.biblomnemon.ui.screens.library.LibraryScreen
import com.ginoskos.biblomnemon.ui.screens.search.SearchScreen
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

enum class NavigationItems(
    val screen: IScreen,
    @param:StringRes val titleRes: Int,
    @param:DrawableRes val iconRes: Int? = null
) {
    Home(HomeScreen, R.string.nav_home, R.drawable.ic_home),
    Library(LibraryScreen, R.string.nav_library, R.drawable.ic_library),
    Search(SearchScreen, R.string.nav_search),
}

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
 * Observe a result returned via [returnResult] from a child destination.
 *
 * @param key The key of the value in the savedStateHandle.
 * @param lifecycleOwner The owner to tie the collection to.
 * @param onResult Called whenever a non-null result is delivered.
 */
fun <T> NavBackStackEntry.observeResult(
    key: String,
    lifecycleOwner: LifecycleOwner,
    onResult: (T) -> Unit
) {
    val savedStateHandle = this.savedStateHandle
    val flow = savedStateHandle.getStateFlow<T?>(key, null)

    lifecycleOwner.lifecycleScope.launch {
        flow.flowWithLifecycle(lifecycleOwner.lifecycle, Lifecycle.State.STARTED)
            .collectLatest { value ->
                if (value != null) {
                    onResult(value)
                    savedStateHandle[key] = null
                }
            }
    }
}