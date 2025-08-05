package com.ginoskos.biblomnemon.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.ginoskos.biblomnemon.ui.navigation.NavigationScreens
import com.ginoskos.biblomnemon.ui.theme.BiblomnemonTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            BiblomnemonTheme {
                NavigationScreens()
            }

//            val screen = currentScreenByRoute(navController)
//            val scope = screen?.let {
//                koinScreenScope(it.identifier)
//            } ?: currentKoinScope()
//
//            BiblomnemonTheme {
//                Scaffold(
//                    modifier = Modifier.Companion.fillMaxSize(),
//                    containerColor = MaterialTheme.colorScheme.background,
//                    topBar = { screen?.TopBar(navController, scope) },
//                    bottomBar = { screen?.BottomBar(navController, scope) },
//                    floatingActionButton = { screen?.Fab(navController, scope) },
//                ) { innerPadding ->
//
//                }
//            }
        }
    }
}