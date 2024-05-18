package com.lovigin.app.skillify.activity

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.ViewGroup
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import androidx.transition.Slide
import androidx.transition.TransitionManager
import com.lovigin.app.skillify.App
import com.lovigin.app.skillify.R
import com.lovigin.app.skillify.navigation.BottomNavigationBar
import com.lovigin.app.skillify.navigation.NavGraph
import com.lovigin.app.skillify.ui.theme.SkillifyTheme

class MainActivity : ComponentActivity() {

    val viewModel = App.userViewModel

    @OptIn(ExperimentalMaterialApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val navController = rememberNavController()
            val pullRefreshState = rememberPullRefreshState(
                refreshing = viewModel.isLoading,
                onRefresh = {
                    Log.d("info", "refreshing")
                })

            SkillifyTheme {
                Scaffold(
                    modifier = Modifier
                        .fillMaxSize(),
//                    topBar = {
//                        HeaderApp(context = this@MainActivity, navController = navController)
//                    },
                    bottomBar = {
                        BottomNavigationBar(navController = navController)
                    }
                ) { innerPadding ->
                    Box(
                        modifier = Modifier
                            .padding(innerPadding)
                            .pullRefresh(pullRefreshState)
                    ) {
                        NavGraph(navHostController = navController, this@MainActivity)
                    }
                }
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HeaderApp(context: Context, navController: NavHostController) {
    TopAppBar(
        title = {
            Image(
                painter = painterResource(id = if (App.isUserPro(App.userViewModel.user.value?.pro)) R.drawable.logopro else R.drawable.logo),
                modifier = Modifier
                    .height(30.dp),
                contentDescription = "Logo",
                alignment = Alignment.CenterStart
            )
        },
        actions = {
            IconButton(
                onClick = {
                    if (App.userViewModel.auth.currentUser != null) {
                        context.startActivity(Intent(context, MessagesActivity::class.java))

                        val slide = Slide(Gravity.START)
                        slide.duration = 500
                        val contentView =
                            (context as Activity).findViewById<ViewGroup>(android.R.id.content)
                        TransitionManager.beginDelayedTransition(contentView, slide)
                    } else {
                        navController.navigate("account")
                    }
                }
            ) {
//                Row {
                Image(
                    painter = painterResource(id = R.drawable.fi_rr_comments),
                    contentDescription = "Search",
                    modifier = Modifier.background(Color.Transparent)
                )
//                    BadgedBox(badge = {
//                        Badge()
//                    }) {}
//                }
            }
        }
    )
}