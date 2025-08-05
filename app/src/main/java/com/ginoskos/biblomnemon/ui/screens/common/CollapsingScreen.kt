package com.ginoskos.biblomnemon.ui.screens.common

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.ginoskos.biblomnemon.ui.screens.AppScreen
import com.ginoskos.biblomnemon.ui.screens.ScreenSurface
import com.ginoskos.biblomnemon.ui.screens.ScreenWrapper

@Composable
fun CollapsingScreen(
    topBar: @Composable () -> Unit = {},
    fab: @Composable () -> Unit = {},
    snackBar: @Composable () -> Unit = {},
    background: @Composable () -> Unit = {},
    content: @Composable () -> Unit
) {
    Box(modifier = Modifier.fillMaxSize()) {
        background()

        AppScreen(
            containerColor = Color.Transparent,
            topBar = topBar,
            fab = fab,
            snackBar = snackBar,
            content = { padding ->
                ScreenSurface(padding) {
                    ScreenWrapper {
                        content()
                    }
                }
            }
        )
    }
}