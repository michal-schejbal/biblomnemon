package com.ginoskos.biblomnemon.ui.navigation

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.ui.graphics.vector.ImageVector
import com.ginoskos.biblomnemon.R
import com.ginoskos.biblomnemon.ui.screens.IScreen
import com.ginoskos.biblomnemon.ui.screens.home.HomeScreen
import com.ginoskos.biblomnemon.ui.screens.search.SearchScreen

enum class NavigationItems(val screen: IScreen, @param:StringRes val titleRes: Int, val icon: ImageVector? = null) {
    Home(HomeScreen, R.string.nav_home, Icons.Default.Home),
    Search(SearchScreen, R.string.nav_search),
}