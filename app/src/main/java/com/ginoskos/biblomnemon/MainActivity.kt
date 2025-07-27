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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.rememberNavController
import com.ginoskos.biblomnemon.core.app.NavigationItems
import com.ginoskos.biblomnemon.ui.navigation.BottomNavigation
import com.ginoskos.biblomnemon.ui.navigation.NavigationGraph
import com.ginoskos.biblomnemon.ui.navigation.TopNavigation
import com.ginoskos.biblomnemon.ui.navigation.currentScreenByRoute
import com.ginoskos.biblomnemon.ui.theme.BiblomnemonTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val navController = rememberNavController()
            var appTitle by remember { mutableStateOf(getString(R.string.app_name)) }
            var selectedTab by remember { mutableStateOf(NavigationItems.Home) }
            val showNavigationBars = currentScreenByRoute(navController)?.isNavigationBarsVisible ?: true

            BiblomnemonTheme {
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    containerColor = MaterialTheme.colorScheme.background,
                    topBar = {
                        if (showNavigationBars) {
                            TopNavigation(
                                navController = navController,
                                title = appTitle
                            )
                        }
                    },
                    bottomBar = {
                        if (showNavigationBars) {
                            BottomNavigation(
                                navController = navController,
                                selected = selectedTab,
                                onSelect = { selectedTab = it }
                            )
                        }
                    }
                ) { innerPadding ->
                    Surface(
                        modifier = Modifier
                            .padding(innerPadding)
                            .fillMaxSize(),
//                        tonalElevation = 1.dp,
                        shadowElevation = 10.dp,
                        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)
                    ) {
                        NavigationGraph(
                            navController = navController,
                            setTopBar = { title ->
                                appTitle = title
                            }
                        )
                    }
                }
            }
        }
    }
}