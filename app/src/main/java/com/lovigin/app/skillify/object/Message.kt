package com.lovigin.app.skillify.`object`

import java.util.UUID

data class Message(
    var id: String = UUID.randomUUID().toString(),
    var lastData: MutableList<String> = ArrayList(),
    var messages: MutableList<Chat>? = ArrayList(),
    var time: Double? = 0.0,
    var uids: List<String> = ArrayList()
) {
    fun toMap(): Map<String, Any> {
        val nonNullMessages = messages?.map { it.toMap() }?.filter { it.isNotEmpty() } ?: emptyList<Map<String, Any>>()
        val map = mutableMapOf<String, Any>(
            "id" to id,
            "lastData" to lastData,
            "messages" to nonNullMessages,
            "time" to (time ?: 0.0),
            "uids" to uids
        )
        return map
    }
}
