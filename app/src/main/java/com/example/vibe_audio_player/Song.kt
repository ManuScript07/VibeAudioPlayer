package com.example.vibe_audio_player

import android.annotation.SuppressLint
import com.example.vibe_audio_player.fragments.PlayerFragment.Companion.musicListPF
import com.example.vibe_audio_player.fragments.PlayerFragment.Companion.songPosition


data class Song(
    val id : String,
    val title: String,
    val album: String,
    val artist : String,
    val duration: Long = 0,
    val dateAdded: Long,
    val size: Long,
    val path: String,
    val artUri: String
)

@SuppressLint("DefaultLocale")
fun formatDuration(duration: Long): String {
    val minutes = duration / 1000 / 60
    val seconds = duration / 1000 % 60
    return String.format("%02d:%02d", minutes, seconds)
}

fun setSongPosition(increment: Boolean){
    val length: Int = musicListPF.size
    if(increment) {
        songPosition = (songPosition + 1) % length
    }
    else {
        if (songPosition == 0)
            songPosition = length - 1
        else
            --songPosition
    }
}



