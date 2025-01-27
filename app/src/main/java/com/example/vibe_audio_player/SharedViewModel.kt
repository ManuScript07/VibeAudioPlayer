package com.example.vibe_audio_player

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SharedViewModel : ViewModel() {
    val songClass = MutableLiveData<String>()
    val songPosition = MutableLiveData<Int>()
    val playlistName = MutableLiveData<String>()
}