package com.lovigin.app.skillify.activity

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Divider
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.lovigin.app.skillify.App
import com.lovigin.app.skillify.R
import com.lovigin.app.skillify.activity.element.ImageComponent
import com.lovigin.app.skillify.`object`.Message
import com.lovigin.app.skillify.`object`.User
import com.lovigin.app.skillify.ui.theme.SkillifyTheme

class MessagesActivity : ComponentActivity() {

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val messagesViewModel by remember {
                mutableStateOf(App.messagesViewModel)
            }

            SkillifyTheme {
                Scaffold(
                    topBar = {
                        TopAppBar(
                            title = { Text(text = stringResource(R.string.messages_str)) },
                            navigationIcon = {
                                IconButton(onClick = { finish() }) {
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
                        modifier = Modifier.padding(innerPadding)
                    ) {
                        items(messagesViewModel.messages) {
                            MessageItemView(it)
                        }
                    }
                }
            }
        }
    }

    @Composable
    fun MessageItemView(message: Message) {
        var imageUrl by remember { mutableStateOf("") }
        var name by remember { mutableStateOf("") }
        var blocked by remember { mutableStateOf(false) }
        var blockedText by remember { mutableStateOf("") }

        val userId: String =
            if (message.uids[0] == App.userViewModel.user.value!!.id) message.uids[1]
            else message.uids[0]

        LaunchedEffect(userId) {
            loadUser(userId) { user ->
                imageUrl = user?.urlAvatar ?: ""
                name = "${user?.first_name ?: ""} ${user?.last_name ?: ""}"
                blocked = user?.blocked!! > 3
                blockedText =
                    if (user.blockedUsers.contains(App.userViewModel.user.value!!.id)) getString(
                        R.string.you_was_blocked_txt,
                        user.first_name
                    )
                    else if (App.userViewModel.user.value!!.blockedUsers.contains(user.id)) getString(
                        R.string.you_blocked_str, user.first_name
                    )
                    else ""
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(5.dp)
                .clickable {
                    if (!blocked) {
                        startActivity(
                            Intent(
                                this@MessagesActivity,
                                ChatActivity::class.java
                            )
                                .putExtra("userId", userId)
                                .putExtra("name", name)
                                .putExtra("imageUrl", imageUrl)
                                .putExtra("idMessage", message.id)
                                .putExtra("blockedText", blockedText)
                        )
                    } else {
                        Toast
                            .makeText(
                                this@MessagesActivity,
                                getString(R.string.user_blocked_txt),
                                Toast.LENGTH_SHORT
                            )
                            .show()
                    }
                },
            verticalAlignment = Alignment.CenterVertically
        ) {
            ImageComponent(url = imageUrl, contentDescription = "Avatar")
            Spacer(modifier = Modifier.width(8.dp))
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(text = name, fontWeight = FontWeight.Bold)

                Row {
                    if (blocked) {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = "Blocked",
                            tint = Color.Red
                        )
                        Text(text = stringResource(R.string.blocked_by_admin_txt))
                    } else {
                        Text(
                            text = if (message.lastData.contains(App.userViewModel.user.value!!.id)) stringResource(
                                R.string.you_txt
                            ) else ""
                        )
                        Text(
                            text = "${
                                message.lastData[1].substring(
                                    0,
                                    minOf(message.lastData[1].length, 50)
                                ).trim()
                            } ${
                                if (message.lastData[1].length > 50) "..." else ""
                            }"
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.width(8.dp))
            if (message.lastData.contains("u") && !message.lastData.contains(App.userViewModel.user.value!!.id)) {
                Box(
                    modifier = Modifier
                        .padding(15.dp)
                        .size(15.dp)
                        .clip(CircleShape)
                        .background(Color.Red.copy(0.5f))
                )
            }
        }
        Divider(modifier = Modifier.padding(horizontal = 15.dp))
    }

    private fun loadUser(id: String, onUserLoaded: (User?) -> Unit) {
        Firebase.firestore
            .collection("users")
            .document(id)
            .get()
            .addOnSuccessListener { documentSnapshot ->
                if (documentSnapshot != null) {
                    val user = documentSnapshot.toObject(User::class.java)
                    onUserLoaded(user)
                } else {
                    onUserLoaded(null)
                }
            }
            .addOnFailureListener {
                onUserLoaded(null)
            }
    }
}