package com.example.networkstatustracker.view

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.example.networkstatustracker.model.NetworkStatusTracker
import com.example.networkstatustracker.model.map
import kotlinx.coroutines.Dispatchers

sealed class MyState {
    object Fetched: MyState()
    object Error: MyState()
}

class MainViewModel(application: Application): AndroidViewModel(application) {
    val networkStatusTracker by lazy { NetworkStatusTracker(getApplication()) }

    val state = networkStatusTracker.networkStatus
        .map(
            onAvailable = { MyState.Fetched },
            onUnavailable = { MyState.Error }
        ).asLiveData(Dispatchers.IO)
}