package com.lovigin.app.skillify.activity

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.lovigin.app.skillify.R
import com.lovigin.app.skillify.model.CallState
import com.lovigin.app.skillify.model.CallViewModel
import io.agora.rtc2.IRtcEngineEventHandler
import io.agora.rtc2.RtcEngine

class CallActivity : ComponentActivity() {
    private lateinit var rtcEngine: RtcEngine
    private val callViewModel: CallViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initializeAgoraEngine()
        setContent {
            CallScreen(callViewModel)
        }
    }

    private fun initializeAgoraEngine() {
        try {
            rtcEngine = RtcEngine.create(baseContext, getString(R.string.agora_key), object : IRtcEngineEventHandler() {
                // Implement necessary callback methods
            })
        } catch (e: Exception) {
            Log.e("CallActivity", "Error initializing Agora engine: ${e.message}")
        }
    }
}

@Composable
fun CallScreen(viewModel: CallViewModel) {
    val callState by viewModel.callState.collectAsState()

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        when (callState) {
            is CallState.Connecting -> Text("Connecting...")
            is CallState.Connected -> {
                // Render video views here
                Button(onClick = { viewModel.endCall() }) {
                    Text("End Call")
                }
            }
            is CallState.Disconnected -> Text("Call Ended")
        }
    }
}