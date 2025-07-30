package com.ginoskos.biblomnemon.ui.screens.home

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.ginoskos.biblomnemon.R
import com.ginoskos.biblomnemon.ui.screens.IScreen
import com.ginoskos.biblomnemon.ui.screens.Screen
import com.ginoskos.biblomnemon.ui.screens.ScreenScaffoldHoist
import com.ginoskos.biblomnemon.ui.screens.ScreenWrapper
import kotlinx.serialization.Serializable

@Screen
object HomeScreen : IScreen {
    @Serializable object Identifier
    override val identifier: Any get() = Identifier
    override val hoist = ScreenScaffoldHoist()

    override fun register(builder: NavGraphBuilder, navController: NavHostController) {
        builder.composable<Identifier> {
            ScreenWrapper {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = stringResource(id = R.string.home_welcome_title, stringResource(id = R.string.app_name)),
                            style = MaterialTheme.typography.displayLarge,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = stringResource(id = R.string.home_welcome_subtitle),
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    }
                }
            }
        }
    }
}