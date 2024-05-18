package com.lovigin.app.skillify.navigation

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.lovigin.app.skillify.ui.theme.BrandBlue

@Composable
fun BottomNavigationBar(
    navController: NavController
) {
    val itemList = listOf(
        NavigationItem.Home,
        NavigationItem.Discover,
        NavigationItem.Profile
    )

    NavigationBar(
        Modifier.clip(RoundedCornerShape(10.dp))
    ) {
        val backStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = backStackEntry?.destination?.route

        itemList.forEach {
            NavigationBarItem(
                selected = currentRoute == it.route,
                onClick = {
                    navController.navigate(it.route)
                },
                icon = {
                    Icon(painter = painterResource(id = it.icon), contentDescription = it.title)
                },
                label = {
                    Text(text = it.title/*, fontSize = 14.sp*/)
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = BrandBlue,
                    unselectedIconColor = Color.Black,
                    selectedTextColor = BrandBlue,
                    unselectedTextColor = Color.Black,
                    indicatorColor = Color.Transparent
                )
            )
        }
    }
}