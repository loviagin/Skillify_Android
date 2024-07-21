package com.lovigin.app.skillify.activity

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.transition.Slide
import android.transition.TransitionManager
import android.view.Gravity
import android.view.ViewGroup
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Text
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.lovigin.app.skillify.App
import com.lovigin.app.skillify.App.Companion.messagesViewModel
import com.lovigin.app.skillify.R
import com.lovigin.app.skillify.activity.ui.theme.SkillifyTheme
import com.lovigin.app.skillify.navigation.BottomNavigationBar
import com.lovigin.app.skillify.navigation.NavGraph

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val navController = rememberNavController()

            SkillifyTheme {
                if (App.userViewModel.auth.currentUser != null &&
                    (App.userViewModel.user.value?.block != null ||
                            (App.userViewModel.user.value?.blocked ?: 0) > 3)
                ) {
                    BlockedUser(
                        reason = App.userViewModel.user.value?.block,
                        context = this@MainActivity
                    )
                } else {
                    Scaffold(
                        modifier = Modifier
                            .fillMaxSize(),
                        bottomBar = {
                            BottomNavigationBar(navController = navController)
                        }
                    ) { innerPadding ->
                        Box(
                            modifier = Modifier
                                .padding(
                                    start = innerPadding.calculateStartPadding(LayoutDirection.Ltr),
                                    bottom = innerPadding.calculateBottomPadding()
                                )
                        ) {
                            NavGraph(navHostController = navController, this@MainActivity)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun BlockedUser(reason: String? = null, context: Context) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.logo),
            contentDescription = "Logo",
            modifier = Modifier.width(150.dp)
        )
        Text("Sorry, you're blocked", modifier = Modifier.padding(vertical = 10.dp))
        if (reason != null) {
            Text("Reason: $reason")
        }
        Text("You can contact us by email: skillify@lovigin.com", modifier = Modifier.clickable {
            val intent = Intent(Intent.ACTION_SENDTO).apply {
                data = Uri.parse("mailto:")
                putExtra(Intent.EXTRA_EMAIL, arrayOf("skillify@lovigin.com"))
                putExtra(Intent.EXTRA_SUBJECT, "Skillify blocked user")
                putExtra(
                    Intent.EXTRA_TEXT,
                    "Message: \n Nickname: ${App.userViewModel.user.value?.nickname ?: ""}"
                )
            }
            context.startActivity(intent)
        })
        Button(onClick = {
            App.userViewModel.logout()
        }, modifier = Modifier.padding(vertical = 10.dp)) {
            Text("Log out", color = Color.White)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HeaderApp(context: Context, navController: NavHostController) {
    TopAppBar(
        modifier = Modifier.padding(top = 0.dp),
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
            Box(
            ) {
                if (messagesViewModel.countUnreadMessages.intValue > 0) {
                    Text(
                        text = " ${if (messagesViewModel.countUnreadMessages.intValue > 9) "9+" else messagesViewModel.countUnreadMessages.intValue.toString()} ",
                        modifier = Modifier
                            .clip(
                                CircleShape
                            )
                            .background(Color.Red)
                            .padding(1.dp),
                        fontSize = 14.sp,
                        color = Color.White,
                    )
                }
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
                    Image(
                        painter = painterResource(id = R.drawable.fi_rr_comments),
                        contentDescription = "Messages",
                        modifier = Modifier.background(Color.Transparent)
                    )
                }
            }
        }
    )
}