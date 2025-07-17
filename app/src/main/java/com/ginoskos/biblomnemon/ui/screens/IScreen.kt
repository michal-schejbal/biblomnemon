package com.ginoskos.biblomnemon.ui.screens

import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder

interface IScreen {
    val identifier: Any

    fun register(builder: NavGraphBuilder, navController: NavController)

    @Composable
    fun Content(navController: NavController)
}