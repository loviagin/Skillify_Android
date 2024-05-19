package com.lovigin.app.skillify.`object`

import java.util.UUID

data class Message(
    var id: String = UUID.randomUUID().toString(),
    var lastData: List<String> = ArrayList(),
    var messages: MutableList<Chat>? = ArrayList(),
    var time: Double? = 0.0,
    var uids: List<String> = ArrayList()
)
