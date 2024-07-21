package com.lovigin.app.skillify.`object`

import com.google.firebase.firestore.PropertyName
import java.util.UUID

data class Chat(
    var id: String = UUID.randomUUID().toString(),
    @PropertyName("cUid") var cUid: String = "",
    var text: String? = null,
    var mediaUrl: String? = null,
    var time: Double = 0.0,
    var status: String? = "u", // u - unread, r - read
    var emoji: String? = null,
    var replyTo: List<String>? = null,
    var type: ChatType? = ChatType.TEXT
) {

    enum class ChatType {
        TEXT,
        IMAGE,
        VIDEO,
        CALL,
        FILE
    }

    fun toMap(): Map<String, Any> {
        val result = mutableMapOf<String, Any>()
        result["id"] = id
        result["cUid"] = cUid
        text?.let { result["text"] = it }
        mediaUrl?.let { result["mediaUrl"] = it }
        result["time"] = time
        status?.let { result["status"] = it }
        emoji?.let { result["emoji"] = it }
        replyTo?.let { result["replyTo"] = it }
        return result
    }

    companion object {
        fun fromMap(map: Map<String, Any>): Chat {
            return Chat(
                id = map["id"] as String,
                cUid = map["cUid"] as String,
                text = map["text"] as String?,
                mediaUrl = map["mediaUrl"] as String?,
                time = map["time"] as Double,
                status = map["status"] as String?,
                emoji = map["emoji"] as String?,
                replyTo = map["replyTo"] as List<String>?
            )
        }
    }
}
