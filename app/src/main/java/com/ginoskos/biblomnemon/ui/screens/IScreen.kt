package com.ginoskos.biblomnemon.ui.screens

import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder

// TODO use KSP processor to generate a screen registry
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.SOURCE)
annotation class Screen

interface IScreen {
    val identifier: Any
    val isNavigationBarsVisible: Boolean get() = true

    fun register(builder: NavGraphBuilder, navController: NavController)

    @Composable
    fun Content(navController: NavController)
}