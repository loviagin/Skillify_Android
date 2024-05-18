package com.lovigin.app.skillify.navigation

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.lovigin.app.skillify.App
import com.lovigin.app.skillify.screen.AccountScreen
import com.lovigin.app.skillify.screen.AuthScreen
import com.lovigin.app.skillify.screen.DiscoverScreen
import com.lovigin.app.skillify.screen.HomeScreen
import com.lovigin.app.skillify.screen.PhoneAuthScreen

@Composable
fun NavGraph(
    navHostController: NavHostController,
    context: Context
) {
    val viewModel = App.userViewModel

    NavHost(navController = navHostController, startDestination = "home") {
        composable(route = "home") {
            HomeScreen(navHostController, context)
        }
        composable(route = "discover") {
            DiscoverScreen(context)
        }
        composable(route = "account") {
            if (viewModel.auth.currentUser != null) AccountScreen(navHostController, context)
            else AuthScreen(navHostController, context)
        }
        composable(route = "phone") {
            PhoneAuthScreen(authViewModel = viewModel, context, navHostController)
        }
    }
}