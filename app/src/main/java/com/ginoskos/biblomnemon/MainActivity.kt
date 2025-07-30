package com.ginoskos.biblomnemon

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.rememberNavController
import com.ginoskos.biblomnemon.ui.navigation.NavigationGraph
import com.ginoskos.biblomnemon.ui.navigation.currentScreenByRoute
import com.ginoskos.biblomnemon.ui.theme.BiblomnemonTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val navController = rememberNavController()
            val currentScreen = currentScreenByRoute(navController)
            val screenScaffoldHoist = currentScreen?.hoist

            BiblomnemonTheme {
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    containerColor = MaterialTheme.colorScheme.background,
                    topBar = { screenScaffoldHoist?.topBar?.invoke(navController) },
                    bottomBar = { screenScaffoldHoist?.bottomBar?.invoke(navController) },
                    floatingActionButton  = { screenScaffoldHoist?.fab?.invoke(navController) },
                    snackbarHost  = { screenScaffoldHoist?.snackBar?.invoke() },
                ) { innerPadding ->
                    Surface(
                        modifier = Modifier
                            .padding(innerPadding)
                            .fillMaxSize(),
//                        tonalElevation = 1.dp,
                        shadowElevation = 10.dp,
                        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)
                    ) {
                        NavigationGraph(navController = navController)
                    }
                }
            }
        }
    }
}