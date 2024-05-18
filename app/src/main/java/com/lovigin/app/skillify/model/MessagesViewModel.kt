package com.lovigin.app.skillify.model

import androidx.compose.runtime.mutableStateOf
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.lovigin.app.skillify.App
import com.lovigin.app.skillify.`object`.Message

class MessagesViewModel {
//    private val viewModel = App.userViewModel
//    private val user = viewModel.user.value

    var messages = mutableStateOf<MutableList<Message>?>(null)

    fun loadMessages() {
        App.userViewModel.user.value?.let {
            if (it.messages.isNotEmpty()) {
                Firebase.firestore
                    .collection("messages")
                    .whereIn("id", it.messages)
                    .get()
                    .addOnSuccessListener { docs ->
                        if (docs != null && !docs.isEmpty) {
                            for (doc in docs) {
                                messages.value?.add(doc.toObject(Message::class.java))
                            }

                        }
                    }
            }
        }
    }

}