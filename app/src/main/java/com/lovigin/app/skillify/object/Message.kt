package com.lovigin.app.skillify.`object`

data class Message(
    var id: String,
    var lastData: List<String>,
    var messages: List<Chat>?,
    var time: Double?,
    var uids: List<String>
)
