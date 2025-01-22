package com.example.vibe_audio_player

import android.app.Service
import android.content.Intent
import android.media.MediaPlayer
import android.os.Binder
import android.os.Handler
import android.os.IBinder
import android.os.Looper

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
            if (mediaPlayer == null) mediaPlayer = MediaPlayer()
            mediaPlayer?.reset()
            mediaPlayer?.setDataSource(PlayerActivity.musicListPA[PlayerActivity.songPosition].path)
            mediaPlayer?.prepare()

            PlayerActivity.binding.playPause.setIconResource(R.drawable.baseline_pause_24)
            PlayerActivity.binding.start.text =
                formatDuration(mediaPlayer!!.currentPosition.toLong())
            PlayerActivity.binding.duration.text =
                formatDuration(mediaPlayer!!.duration.toLong())
            PlayerActivity.binding.seekBarPA.progress = 0
            PlayerActivity.binding.seekBarPA.max = mediaPlayer!!.duration
            PlayerActivity.nowPlayingId = PlayerActivity.musicListPA[PlayerActivity.songPosition].id
//            PlayerActivity.loudnessEnhancer = LoudnessEnhancer(mediaPlayer!!.audioSessionId)
//            PlayerActivity.loudnessEnhancer.enabled = true
        } catch (e: Exception) {
            return
        }
    }

    fun seekBarSetup() {
        runnable = Runnable {
            if (!PlayerActivity.binding.seekBarPA.isPressed) { // Обновляем только если пользователь не трогает SeekBar
                PlayerActivity.binding.start.text = formatDuration(mediaPlayer!!.currentPosition.toLong())
                PlayerActivity.binding.seekBarPA.progress = mediaPlayer!!.currentPosition
            }
            Handler(Looper.getMainLooper()).postDelayed(runnable, 500) // Увеличиваем интервал для снижения нагрузки
        }
        Handler(Looper.getMainLooper()).postDelayed(runnable, 0)
    }
}

