package com.lovigin.app.skillify.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowCompat
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.lovigin.app.skillify.App
import com.lovigin.app.skillify.App.Companion.messagesViewModel
import com.lovigin.app.skillify.R
import com.lovigin.app.skillify.activity.element.BackButton
import com.lovigin.app.skillify.activity.element.ImageComponent
import com.lovigin.app.skillify.activity.ui.theme.SkillifyTheme
import com.lovigin.app.skillify.`object`.Chat
import com.lovigin.app.skillify.`object`.Message
import com.lovigin.app.skillify.ui.theme.BrandBlue
import com.lovigin.app.skillify.worker.NotificationSender
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

class ChatActivity : ComponentActivity() {

    var messages = mutableStateListOf<Chat>()

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        WindowCompat.setDecorFitsSystemWindows(window, false)
        setContent {
            var userId by remember { mutableStateOf(intent.getStringExtra("userId") ?: "") }
            var name by remember { mutableStateOf(intent.getStringExtra("name") ?: "") }
            var imageUrl by remember { mutableStateOf(intent.getStringExtra("imageUrl") ?: "") }
            var idMessage by remember { mutableStateOf(intent.getStringExtra("idMessage") ?: "") }

            var messageText by remember { mutableStateOf("") }
            val listState = rememberLazyListState()
            val coroutineScope = rememberCoroutineScope()

            LaunchedEffect(Unit) {
                loadChats(idMessage) {
                    coroutineScope.launch {
                        snapshotFlow { messages.size }
                            .collect {
                                if (messages.isNotEmpty()) {
                                    listState.scrollToItem(messages.size - 1)
                                }
                            }
                    }
                }
            }

            SkillifyTheme {
                Scaffold(
                    modifier = Modifier
                        .fillMaxSize()
                        .imePadding()
                        .systemBarsPadding(),
                    topBar = {
                        TopAppBar(title = {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.clickable {
                                    startActivity(
                                        Intent(
                                            this@ChatActivity,
                                            ProfileActivity::class.java
                                        ).putExtra("idUser", userId)
                                    )
                                }
                            ) {
                                ImageComponent(
                                    url = imageUrl,
                                    contentDescription = "Avatar",
                                    size = 40.dp,
                                    padding = 0.dp,
                                    defaultSize = 40.dp
                                )
                                Text(text = name, modifier = Modifier.padding(start = 15.dp))
                            }
                        }, navigationIcon = {
                            BackButton {
                                finish()
                            }
                        },

//                            actions = {
//                                IconButton(
//                                    onClick = {
//                                        startActivity(
//                                            Intent(
//                                                this@ChatActivity,
//                                                MessagesActivity::class.java
//                                            )
//                                        )
//                                    },
//                                    modifier = Modifier.padding(horizontal = 1.dp)
//                                ) {
//                                    Icon(
//                                        imageVector = Icons.Default.Add,
//                                        contentDescription = "Send"
//                                    )
//                                }
//                            }
                        )
                    }, bottomBar = {
                        BottomAppBar {
                            if (intent.getStringExtra("blockedText")!!.isNotEmpty()) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.Center,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Lock,
                                        contentDescription = "Lock",
                                        tint = Color.Red
                                    )
                                    Text(
                                        text = intent.getStringExtra("blockedText")!!,
                                        modifier = Modifier.padding(horizontal = 10.dp)
                                    )
                                }
                            } else {
                                IconButton(
                                    onClick = {
                                        Toast.makeText(
                                            this@ChatActivity,
                                            getString(R.string.in_development_str),
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    },
                                    modifier = Modifier.padding(horizontal = 1.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Add,
                                        contentDescription = "Media"
                                    )
                                }
                                OutlinedTextField(
                                    value = messageText,
                                    onValueChange = { messageText = it },
                                    modifier = Modifier
                                        .weight(1f),
                                    placeholder = { Text(stringResource(R.string.enter_message_txt)) },
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedContainerColor = if (isSystemInDarkTheme()) Color.Black.copy(
                                            0.5f
                                        ) else Color.White,
                                        unfocusedContainerColor = if (isSystemInDarkTheme()) Color.Black.copy(
                                            0.5f
                                        ) else Color.White,
                                        focusedBorderColor = BrandBlue.copy(0.3f),
                                        unfocusedBorderColor = Color.Gray.copy(0.5f),
                                    ),
                                    maxLines = 3,
                                    shape = RoundedCornerShape(30.dp)
                                )
                                IconButton(
                                    onClick = {
                                        if (messageText.isNotEmpty()) {
                                            val c = Chat(
                                                cUid = App.userViewModel.user.value!!.id,
                                                text = messageText,
                                                time = System.currentTimeMillis() / 1000.0,
                                                status = "u"
                                            )

                                            messages.add(c)
                                            messagesViewModel.sendMessage(c, idMessage)
                                        }
                                        NotificationSender.sendNotification(
                                            this@ChatActivity,
                                            userId,
                                            messageText,
                                            name
                                        )

                                        messageText = ""
                                    },
                                    modifier = Modifier.padding(horizontal = 1.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.Send,
                                        contentDescription = "Send"
                                    )
                                }
                            }
                        }
                    }) { innerPadding ->
                    Box(
                        modifier = Modifier
                            .padding(innerPadding)
                    ) {
                        Image(
                            painter = painterResource(id = if (isSystemInDarkTheme()) R.drawable.chatdark else R.drawable.chatlight),
                            contentDescription = "Background Image",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                        LazyColumn(
                            state = listState
                        ) {
                            messages.forEach { chat ->
                                item {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(horizontal = 15.dp, vertical = 10.dp),
                                        horizontalArrangement = if (chat.cUid == App.userViewModel.user.value?.id) Arrangement.End else Arrangement.Start
                                    ) {
                                        ChatItemView(chat)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    @Composable
    fun ChatItemView(chat: Chat) {
        Column(
            modifier = Modifier
                .widthIn(min = 100.dp, max = 320.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(
                    if (chat.cUid == App.userViewModel.user.value?.id) BrandBlue
                    else Color.White
                )
                .padding(15.dp),
            horizontalAlignment = if (chat.cUid == App.userViewModel.user.value?.id) Alignment.End else Alignment.Start
        ) {
            chat.text?.let {
                Text(
                    text = it,
                    color = if (chat.cUid == App.userViewModel.user.value?.id) Color.White else Color.Black
                )
            }
            Row {
                Text(
                    text = formatTime(chat.time),
                    color = if (chat.cUid == App.userViewModel.user.value?.id) Color.White else Color.Black,
                    fontSize = 13.sp
                )
                if (chat.cUid == App.userViewModel.user.value?.id) {
                    if (chat.status == "r") {
                        Icon(
                            painter = painterResource(id = R.drawable.fi_rr_double_check),
                            contentDescription = "Check",
                            tint = Color.White,
                            modifier = Modifier
                                .padding(start = 5.dp)
                                .size(20.dp)
                        )
                    } else {
                        Icon(
                            painter = painterResource(id = R.drawable.fi_rr_check),
                            contentDescription = "Check",
                            tint = Color.White,
                            modifier = Modifier
                                .padding(start = 5.dp)
                                .size(20.dp)
                        )
                    }
                }
            }
        }
    }

    private fun loadChats(idMessage: String, onMessagesLoaded: () -> Unit) {
        Firebase.firestore
            .collection("messages")
            .document(idMessage)
            .addSnapshotListener { value, error ->
                if (error != null) {
                    return@addSnapshotListener
                }
                if (value != null && value.exists()) {
                    val message = value.toObject(Message::class.java)
                    messages.clear()

                    message?.messages?.forEach { chat ->
                        if (chat.cUid != App.userViewModel.user.value?.id && chat.status == "u") {
                            chat.status = "r"
                        }
                        messages.add(chat)
                    }

                    Firebase.firestore.collection("messages")
                        .document(idMessage)
                        .update("messages", message?.messages?.map { it.toMap() })
                        .addOnSuccessListener {
                            onMessagesLoaded()
                        }
                        .addOnFailureListener { e ->
                            Log.e("FirestoreError", "Error updating messages", e)
                        }
                }
            }
    }

    private fun formatTime(timeInSeconds: Double): String {
        // Преобразуем время в миллисекундах
        val timeInMillis = (timeInSeconds * 1000).toLong()
        val time =
            LocalDateTime.ofInstant(Instant.ofEpochMilli(timeInMillis), ZoneId.systemDefault())
        val now = LocalDateTime.now()
        val formatterTime = DateTimeFormatter.ofPattern("HH:mm")
        val formatterDate = DateTimeFormatter.ofPattern("dd MMM", Locale("en"))

        return when {
            time.toLocalDate() == now.toLocalDate() -> {
                getString(R.string.today_txt, time.format(formatterTime))
            }

            time.toLocalDate() == now.minusDays(1).toLocalDate() -> {
                getString(R.string.yesterday_txt, time.format(formatterTime))
            }

            time.toLocalDate()
                .isAfter(now.minusDays(now.dayOfWeek.value.toLong()).toLocalDate()) -> {
                "${
                    time.dayOfWeek.getDisplayName(
                        java.time.format.TextStyle.FULL,
                        Locale("en")
                    )
                }, ${time.format(formatterTime)}"
            }

            else -> {
                "${time.format(formatterDate)}, ${time.format(formatterTime)}"
            }
        }
    }
}