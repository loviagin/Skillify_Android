package com.lovigin.app.skillify.worker

import android.content.Context
import com.lovigin.app.skillify.R
import org.json.JSONArray
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

class NotificationSender {

    companion object {
        fun sendNotification(context: Context, userId: String, message: String) {
            val json = JSONObject()
            json.put("app_id", context.getString(R.string.onesignal_app_id))
            json.put("include_external_user_ids", JSONArray().put(userId))
            json.put("contents", JSONObject().put("en", message))

            val thread = Thread {
                try {
                    val url = URL("https://onesignal.com/api/v1/notifications")
                    val con = url.openConnection() as HttpURLConnection
                    con.apply {
                        requestMethod = "POST"
                        setRequestProperty("Content-Type", "application/json; charset=UTF-8")
                        setRequestProperty("Authorization",
                            context.getString(R.string.onesignal_key))
                        doOutput = true
                        val outputStream = outputStream
                        outputStream.write(json.toString().toByteArray(charset("UTF-8")))
                        outputStream.close()
                        val responseCode = responseCode
                        println("Response Code : $responseCode")
                        BufferedReader(InputStreamReader(inputStream)).use {
                            var inputLine: String?
                            while (it.readLine().also { line -> inputLine = line } != null) {
                                println(inputLine)
                            }
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
            thread.start()
        }
    }
}