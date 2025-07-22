package com.ginoskos.biblomnemon.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import com.ginoskos.biblomnemon.core.app.NavigationItems
import com.ginoskos.biblomnemon.ui.screens.home.HomeScreen

private val screens = NavigationItems.entries.map { it.screen }

@Composable
fun NavigationGraph(modifier: Modifier = Modifier, navController: NavHostController, setTopBar: (title: String) -> Unit) {
    NavHost(navController = navController, startDestination = HomeScreen.Identifier) {
        screens.forEach { screen ->
            screen.register(this, navController)
        }
    }
}