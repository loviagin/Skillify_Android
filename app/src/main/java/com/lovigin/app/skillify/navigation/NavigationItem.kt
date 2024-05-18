package com.lovigin.app.skillify.navigation

import com.lovigin.app.skillify.R

sealed class NavigationItem (val title: String, val icon: Int, val route: String) {
    data object Home : NavigationItem("Home", R.drawable.fi_rr_home, "home")
    data object  Discover: NavigationItem("Discover", R.drawable.fi_rr_search, "discover")
    data object Profile : NavigationItem("Account", R.drawable.fi_rr_user, "account")
}