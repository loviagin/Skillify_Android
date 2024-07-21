package com.lovigin.app.skillify

import android.app.Application
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import com.google.firebase.FirebaseApp
import com.lovigin.app.skillify.model.MessagesViewModel
import com.lovigin.app.skillify.model.UserViewModel
import com.lovigin.app.skillify.service.CallService
import com.onesignal.OneSignal
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.temporal.ChronoUnit

class App : Application() {

    companion object {
        fun isUserPro(date: Double?): Boolean {
            return (date ?: 0.0) > System.currentTimeMillis() / 1000
        }

        fun isOld(birthDateMillis: Double): Boolean =
            ChronoUnit.YEARS.between(
                Instant.ofEpochMilli(birthDateMillis.toLong()).atZone(ZoneId.systemDefault())
                    .toLocalDate(), LocalDate.now()
            ) >= 12

        fun getDeviceInfo(): String {
            val manufacturer = Build.MANUFACTURER
            val model = Build.MODEL
            val brand = Build.BRAND
            val device = Build.DEVICE
            val product = Build.PRODUCT
            val androidVersion = Build.VERSION.RELEASE
            val sdkVersion = Build.VERSION.SDK_INT
            val hardware = Build.HARDWARE
            val fingerprint = Build.FINGERPRINT

            return """
         Manufacturer: $manufacturer
         Model: $model
         Brand: $brand
         Device: $device
         Product: $product
         Android Version: $androidVersion
         SDK Version: $sdkVersion
         Hardware: $hardware
         Fingerprint: $fingerprint
    """.trimIndent()
        }


        lateinit var userViewModel: UserViewModel
        lateinit var messagesViewModel: MessagesViewModel
        lateinit var sharedPreferences: SharedPreferences
    }

    override fun onCreate() {
        super.onCreate()

        // Initialize Firebase
        FirebaseApp.initializeApp(this)

        // OneSignal Initialization
        OneSignal.initWithContext(this, getString(R.string.onesignal_app_id))

        CoroutineScope(Dispatchers.IO).launch {
            OneSignal.Notifications.requestPermission(true)
        }

        userViewModel = UserViewModel()
        messagesViewModel = MessagesViewModel()
        sharedPreferences = getSharedPreferences("sharedPrefs", Context.MODE_PRIVATE)

        if (userViewModel.auth.currentUser != null) {
            userViewModel.loadUser()
        }
    }
}

val avatars = arrayOf(
    "avatar1",
    "avatar2",
    "avatar3",
    "avatar4",
    "avatar5",
    "avatar6",
    "avatar7",
    "avatar8",
    "avatar9",
    "avatar10",
    "avatar11",
    "avatar12",
    "avatar13",
    "avatar14",
    "avatar15",
    "avatar16"
)
//val covers    =  arrayOf("cover:1", "cover:2", "cover:3", "cover:4")
//val emojies   =  arrayOf("sunglasses", "sparkles", "flame", "fireworks", "snowflake", "bolt", "paperplane", "link", "sun.min", "moon")
//val statuses  =  arrayOf("star.fill", "moon.stars", "ellipsis.message", "phone.badge.waveform.fill", "flame.fill", "bolt.fill", "laptopcomputer", "graduationcap.fill", "beach.umbrella.fill", "cup.and.saucer.fill")
//val chatTheme =  arrayOf("theme1", "theme2")