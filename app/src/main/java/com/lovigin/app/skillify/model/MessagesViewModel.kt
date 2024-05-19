package com.lovigin.app.skillify.model

import android.util.Log
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.lovigin.app.skillify.App
import com.lovigin.app.skillify.`object`.Chat
import com.lovigin.app.skillify.`object`.Message
import com.lovigin.app.skillify.worker.NotificationSender

class MessagesViewModel {
//    private val viewModel = App.userViewModel
//    private val user = viewModel.user.value

    var messages = mutableStateListOf<Message>()
    var countUnreadMessages = mutableIntStateOf(0)

    init {
        loadMessages()
    }

    fun loadMessages() {
        App.userViewModel.user.value?.let { user ->
            if (user.messages.isNotEmpty()) {
                // Преобразуем messages в список идентификаторов чатов
                val chatIds = user.messages.flatMap { it.values }

                Firebase.firestore
                    .collection("messages")
                    .whereIn("id", chatIds)
                    .addSnapshotListener { docs, e ->
                        if (e != null) {
                            Log.w("TAG", "Listen failed.", e)
                            return@addSnapshotListener
                        }

                        if (docs != null && !docs.isEmpty) {
                            countUnreadMessages.intValue = 0
                            messages.clear()

                            for (doc in docs) {
                                val m = doc.toObject(Message::class.java)
                                messages.add(m)
                                if (!m.lastData.contains(user.id) && m.lastData.contains("u")) {
                                    countUnreadMessages.intValue++
                                }
                            }
                            messages.sortByDescending { it.time }
//                            sharedPreferences.edit()
//                                .putInt("countUnreadMessages", countUnreadMessages.intValue).apply()
                        }
                    }
            }
        }
    }

    fun sendMessage(chat: Chat, idMessage: String) {
        Firebase.firestore
            .collection("messages")
            .document(idMessage)
            .update("messages", FieldValue.arrayUnion(chat.toMap()))

        Firebase.firestore
            .collection("messages")
            .document(idMessage)
            .update("lastData", listOf(App.userViewModel.user.value!!.id, chat.text, "u"))

        Firebase.firestore
            .collection("messages")
            .document(idMessage)
            .update("time", System.currentTimeMillis() / 1000.0)
    }
}