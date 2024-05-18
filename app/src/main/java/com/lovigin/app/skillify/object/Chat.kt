package com.lovigin.app.skillify.`object`

data class Chat(
    var id: String,
    var cUid: String,
    var text: String?,
    var mediaUrl: String? = null,
    var time: Double,
    var status: String? = "u", // u - unread, r - read
    var emoji: String? = null,
    var replyTo: List<String>?
)
