package com.example.vibe_audio_player

import android.annotation.SuppressLint


data class Song(
    val id : String,
    val title: String,
    val album: String,
    val artist : String,
    val duration: Long = 0,
    val path: String,
    val artUri: String
) {

    @SuppressLint("DefaultLocale")
    fun formatDuration(duration: Long): String {
        val minutes = duration / 1000 / 60
        val seconds = duration / 1000 % 60
        return String.format("%02d:%02d", minutes, seconds)
    }
}

