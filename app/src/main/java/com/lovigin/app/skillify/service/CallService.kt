package com.lovigin.app.skillify.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.lovigin.app.skillify.R
import com.lovigin.app.skillify.activity.CallActivity

class CallService : Service() {
    private lateinit var notificationManager: NotificationManager
    private lateinit var callNotificationBuilder: NotificationCompat.Builder

    override fun onCreate() {
        super.onCreate()
        notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        createNotificationChannel()
        callNotificationBuilder = createCallNotification()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            "INCOMING_CALL" -> showIncomingCallNotification(intent)
            "ACCEPT_CALL" -> acceptCall()
            "REJECT_CALL" -> rejectCall()
        }
        return START_STICKY
    }

    private fun createNotificationChannel() {
        val channel = NotificationChannel(
            "call_channel",
            "Incoming Calls",
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            setSound(null, null)
            enableLights(true)
            lightColor = Color.RED
            enableVibration(true)
            description = "Notifications for incoming calls"
        }
        notificationManager.createNotificationChannel(channel)
    }

    private fun createCallNotification(): NotificationCompat.Builder {
        val acceptIntent = Intent(this, CallService::class.java).apply {
            action = "ACCEPT_CALL"
        }
        val rejectIntent = Intent(this, CallService::class.java).apply {
            action = "REJECT_CALL"
        }

        return NotificationCompat.Builder(this, "call_channel")
            .setSmallIcon(R.drawable.favicon)
            .setContentTitle("Incoming Call")
            .setContentText("Someone is calling you")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_CALL)
            .setFullScreenIntent(
                PendingIntent.getActivity(
                    this,
                    0,
                    Intent(this, CallActivity::class.java),
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                ),
                true
            )
            .addAction(
                R.drawable.ic_accept,
                "Accept",
                PendingIntent.getService(
                    this,
                    0,
                    acceptIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                )
            )
            .addAction(
                R.drawable.ic_reject,
                "Reject",
                PendingIntent.getService(
                    this,
                    0,
                    rejectIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                )
            )
    }

    private fun showIncomingCallNotification(intent: Intent) {
        val callerId = intent.getStringExtra("caller_id")
        val notification = callNotificationBuilder
            .setContentText("Incoming call from $callerId")
            .build()
        startForeground(CALL_NOTIFICATION_ID, notification)
    }

    private fun acceptCall() {
        // Implement call acceptance logic
        stopForeground(true)
        startActivity(Intent(this, CallActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        })
    }

    private fun rejectCall() {
        // Implement call rejection logic
        stopForeground(true)
    }

    override fun onBind(intent: Intent?): IBinder? = null

    companion object {
        const val CALL_NOTIFICATION_ID = 1
    }
}