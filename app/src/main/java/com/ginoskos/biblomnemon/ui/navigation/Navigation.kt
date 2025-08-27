package com.ginoskos.biblomnemon.ui.navigation

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.ginoskos.biblomnemon.R
import com.ginoskos.biblomnemon.ui.screens.activity.ReadingActivityEditScreen
import com.ginoskos.biblomnemon.ui.screens.activity.ReadingActivityScreen
import com.ginoskos.biblomnemon.ui.screens.home.HomeScreen
import com.ginoskos.biblomnemon.ui.screens.library.LibraryEditScreen
import com.ginoskos.biblomnemon.ui.screens.library.LibraryScreen
import com.ginoskos.biblomnemon.ui.screens.profile.ProfileScreen
import com.ginoskos.biblomnemon.ui.screens.scanner.ScannerScreen
import com.ginoskos.biblomnemon.ui.screens.search.SearchDetailScreen
import com.ginoskos.biblomnemon.ui.screens.search.SearchScreen
import kotlinx.serialization.Serializable


@Serializable
sealed class NavigationRoute {
    @Serializable data object Home : NavigationRoute()

    @Serializable data object Profile : NavigationRoute()

    @Serializable data object Scanner : NavigationRoute()

    @Serializable data object Library : NavigationRoute()
    @Serializable data object LibraryEdit : NavigationRoute()

    @Serializable data object Search : NavigationRoute()
    @Serializable data object SearchDetail : NavigationRoute()
    
    @Serializable data object ReadingActivity : NavigationRoute()
    @Serializable data object ReadingActivityEdit : NavigationRoute()
}

enum class BottomNavigationItems(
    val route: NavigationRoute,
    @param:StringRes val titleRes: Int,
    @param:DrawableRes val iconRes: Int? = null
) {
    Home(NavigationRoute.Home, R.string.nav_home, R.drawable.ic_home),
    Library(NavigationRoute.Library, R.string.nav_library, R.drawable.ic_library),
    Activity(NavigationRoute.ReadingActivity, R.string.nav_activity, R.drawable.ic_nav_activity),
}

@Composable
fun NavigationScreens() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = NavigationRoute.Home) {
        composable<NavigationRoute.Home> {
            HomeScreen(navController)
        }

        composable<NavigationRoute.Profile> {
            ProfileScreen(navController)
        }

        composable<NavigationRoute.Scanner> {
            ScannerScreen(navController)
        }

        // Search
        composable<NavigationRoute.Search> {
            SearchScreen(navController)
        }
        composable<NavigationRoute.SearchDetail> {
            SearchDetailScreen(navController)
        }

        // Library
        composable<NavigationRoute.Library> {
            LibraryScreen(navController)
        }
        composable<NavigationRoute.LibraryEdit> {
            LibraryEditScreen(navController)
        }

        // Activity
        composable<NavigationRoute.ReadingActivity> {
            ReadingActivityScreen(navController)
        }
        composable<NavigationRoute.ReadingActivityEdit> {
            ReadingActivityEditScreen(navController)
        }

    }
}