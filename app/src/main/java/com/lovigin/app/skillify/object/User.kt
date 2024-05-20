package com.lovigin.app.skillify.`object`

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.Date
import java.util.LinkedList

data class User(
    var id: String = "",
    var first_name: String = "",
    var last_name: String = "",
    var email: String = "",
    var bio: String = "",
    var language: String = "en",
    var blocked: Int = 0,
    var nickname: String = "",
    var phone: String = "",
    var urlAvatar: String = "",
    var online: Boolean = true,
    var sex: String = "-",
    var birthday: Date = getBirthday(),
    var pro: Double = 0.0,
    var favorites: MutableList<Favorite> = ArrayList(),
    var calls: MutableList<MutableMap<String, String>> = LinkedList(),
    var messages: MutableList<MutableMap<String, String>> = LinkedList(),
    var learningSkills: MutableList<Skill> = ArrayList(),
    var selfSkills: MutableList<Skill> = ArrayList(),
    var blockedUsers: MutableList<String> = ArrayList(),
    var devices: MutableList<String> = ArrayList(),
    var notifications: MutableList<String> = LinkedList(),
    var subscriptions: MutableList<String> = ArrayList(),
    var subscribers: MutableList<String> = ArrayList(),
    var lastData: List<String>? = listOf(
        "android",
        DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss").format(LocalDateTime.now()),
        "3 ver. 1.0.3"
    ),
    var tags: List<String>? = listOf("user")
) {
    companion object {
        private fun getBirthday(): Date {
            val currentYear = Calendar.getInstance().get(Calendar.YEAR)
            val calendar = Calendar.getInstance().apply {
                set(currentYear - 13, Calendar.JANUARY, 1)
            }
            return calendar.time
        }
    }
}