package com.ginoskos.biblomnemon.ui.screens.home

import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.ginoskos.biblomnemon.ui.screens.IScreen
import kotlinx.serialization.Serializable

object HomeScreen : IScreen {
    @Serializable object Identifier
    override val identifier: Any
        get() = Identifier

    override fun register(builder: NavGraphBuilder, navController: NavController) {
        builder.composable<Identifier> {
            Content(
                navController = navController
            )
        }
    }

    @Composable
    override fun Content(navController: NavController) {

    }
}