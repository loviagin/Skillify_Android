package com.lovigin.app.skillify

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import androidx.compose.runtime.mutableStateOf
import com.google.firebase.FirebaseApp
import com.lovigin.app.skillify.model.MessagesViewModel
import com.lovigin.app.skillify.model.UserViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import com.onesignal.OneSignal
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
            ChronoUnit.YEARS.between(Instant.ofEpochMilli(birthDateMillis.toLong()).atZone(ZoneId.systemDefault()).toLocalDate(), LocalDate.now()) >= 12

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
