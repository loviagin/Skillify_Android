package com.lovigin.app.skillify.activity

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.lovigin.app.skillify.App
import com.lovigin.app.skillify.activity.element.AvatarComponent
import com.lovigin.app.skillify.activity.element.FollowUserView
import com.lovigin.app.skillify.activity.element.ImageComponent
import com.lovigin.app.skillify.activity.ui.theme.SkillifyTheme
import com.lovigin.app.skillify.`object`.User
import com.lovigin.app.skillify.ui.theme.BrandBlue

class FollowActivity : ComponentActivity() {

    private var user = mutableStateOf(User())
    private var subscribers = mutableStateListOf<User>()
    private var subscriptions = mutableStateListOf<User>()

    private fun loadUser(id: String) {
        Firebase.firestore
            .collection("users")
            .document(id)
            .get()
            .addOnSuccessListener {
                if (it.exists()) {
                    user.value = it.toObject(User::class.java)!!
                }
            }
    }

    private fun loadSubscribers() {
        if (user.value.subscribers.isNotEmpty()) {
            Firebase.firestore
                .collection("users")
                .whereIn("id", user.value.subscribers)
                .get()
                .addOnSuccessListener {
                    if (!it.isEmpty) {
                        subscribers.clear()
                        for (doc in it) {
                            subscribers.add(doc.toObject(User::class.java))
                        }
                    }
                }
        }
    }

    private fun loadSubscriptions() {
        if (user.value.subscriptions.isNotEmpty()) {
            Firebase.firestore
                .collection("users")
                .whereIn("id", user.value.subscriptions)
                .get()
                .addOnSuccessListener {
                    if (!it.isEmpty) {
                        subscriptions.clear()
                        for (doc in it) {
                            subscriptions.add(doc.toObject(User::class.java))
                        }
                    }
                }
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val type = intent.getIntExtra("type", 0)
            val id = intent.getStringExtra("idUser")
            var selectedTab by remember { mutableIntStateOf(type) }

            loadUser(id!!)
            if (type == 0) {
                loadSubscribers()
            } else {
                loadSubscriptions()
            }

            SkillifyTheme {
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    topBar = {
                        TopAppBar(title = { Text(text = "@${user.value.nickname}") },
                            navigationIcon = {
                                IconButton(onClick = {
                                    finish()
                                }) {
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                        contentDescription = "Back"
                                    )
                                }
                            }
                        )
                    }
                ) { innerPadding ->
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                    ) {
                        item {
                            TabRow(selectedTabIndex = selectedTab) {
                                Tab(
                                    selected = selectedTab == 0,
                                    onClick = {
                                        selectedTab = 0
                                        loadSubscribers()
                                    },
                                    unselectedContentColor = Color.Black
                                ) {
                                    Text(
                                        text = "Subscribers",
                                        modifier = Modifier.padding(15.dp),
                                    )
                                }
                                Tab(
                                    selected = selectedTab == 1,
                                    onClick = {
                                        selectedTab = 1
                                        loadSubscriptions()
                                    },
//                                    modifier = Modifier.padding(15.dp),
                                    unselectedContentColor = Color.Black
                                ) {
                                    Text(
                                        text = "Subscriptions",
                                        modifier = Modifier.padding(15.dp),
                                    )
                                }
                            }
                        }

                        if (selectedTab == 0) {
                            items(subscribers) {
                                FollowUserView(context = this@FollowActivity, user = it)
                            }
                        } else {
                            items(subscriptions) {
                                FollowUserView(context = this@FollowActivity, user = it)
                            }
                        }
                    }
                }
            }
        }
    }
}
