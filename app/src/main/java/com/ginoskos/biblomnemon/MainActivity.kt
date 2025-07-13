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
import com.ginoskos.biblomnemon.ui.BottomNavItem
import com.ginoskos.biblomnemon.ui.BottomNavigation
import com.ginoskos.biblomnemon.ui.TopNavigation
import com.ginoskos.biblomnemon.ui.search.SearchScreen
import com.ginoskos.biblomnemon.ui.theme.BiblomnemonTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val appTitle by remember { mutableStateOf(getString(R.string.app_name)) }
            var selectedTab by remember { mutableStateOf<BottomNavItem>(BottomNavItem.Search) }

            BiblomnemonTheme {
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    containerColor = MaterialTheme.colorScheme.background,
                    topBar = {
                        TopNavigation(
                            title = appTitle,
                            onProfileClick = {  },
                            onAddClick = {  },
                            onSearchClick  = {  }
                        )
                    },
                    bottomBar = {
                        BottomNavigation(
                            selected = selectedTab,
                            onSelect = { selectedTab = it }
                        )
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
                        SearchScreen(
                            onClick = {}
                        )
                    }
                }
            }
        }
    }
}