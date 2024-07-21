package com.lovigin.app.skillify.model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class CallViewModel : ViewModel() {
    private val _callState = MutableStateFlow<CallState>(CallState.Disconnected)
    val callState: StateFlow<CallState> = _callState

    fun startCall(channelName: String) {
        viewModelScope.launch {
            _callState.value = CallState.Connecting
            // Implement Agora joining channel logic here
            _callState.value = CallState.Connected
        }
    }

    fun endCall() {
        viewModelScope.launch {
            // Implement Agora leaving channel logic here
            _callState.value = CallState.Disconnected
        }
    }
}

sealed class CallState {
    object Connecting : CallState()
    object Connected : CallState()
    object Disconnected : CallState()
}