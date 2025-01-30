package com.example.vibe_audio_player.services

import android.app.Service
import android.content.Intent
import android.media.MediaPlayer
import android.os.Binder
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import com.example.vibe_audio_player.R
import com.example.vibe_audio_player.formatDuration
import com.example.vibe_audio_player.fragments.PlayerFragment

class MusicService: Service() {

    private var myBinder = MyBinder()
    var mediaPlayer: MediaPlayer? = null
    private lateinit var runnable: Runnable


    override fun onBind(p0: Intent?): IBinder {
        return myBinder
    }


    inner class MyBinder: Binder(){
        fun currentService(): MusicService {
            return this@MusicService
        }
    }


    fun createMediaPlayer() {
        try {
            if (mediaPlayer == null)
                mediaPlayer = MediaPlayer()
            mediaPlayer?.reset()
            mediaPlayer?.setDataSource(PlayerFragment.musicListPF[PlayerFragment.songPosition].path)
            mediaPlayer?.prepare()

            PlayerFragment.binding.playPause.setImageResource(R.drawable.baseline_pause_48)
            PlayerFragment.binding.start.text =
                formatDuration(mediaPlayer!!.currentPosition.toLong())
            PlayerFragment.binding.duration.text =
                formatDuration(mediaPlayer!!.duration.toLong())
            PlayerFragment.binding.seekBar.progress = 0
            PlayerFragment.binding.seekBar.max = mediaPlayer!!.duration

            PlayerFragment.nowPlayingId = PlayerFragment.musicListPF[PlayerFragment.songPosition].id

        } catch (e: Exception) {
            return
        }
    }

    fun seekBarSetup() {
        runnable = Runnable {
            if (!PlayerFragment.binding.seekBar.isPressed) { // Обновляем только если пользователь не трогает SeekBar
                PlayerFragment.binding.start.text = formatDuration(mediaPlayer!!.currentPosition.toLong())
                PlayerFragment.binding.seekBar.progress = mediaPlayer!!.currentPosition
            }
            Handler(Looper.getMainLooper()).postDelayed(runnable, 500) // Увеличиваем интервал для снижения нагрузки
        }
        Handler(Looper.getMainLooper()).postDelayed(runnable, 0)
    }
}

