package com.example.vibe_audio_player

import android.annotation.SuppressLint
import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.media.MediaPlayer
import android.os.Bundle
import android.os.IBinder
import android.widget.SeekBar
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.vibe_audio_player.databinding.ActivityPlayerBinding


class PlayerActivity : AppCompatActivity(), ServiceConnection, MediaPlayer.OnCompletionListener {

    companion object{
        lateinit var musicListPA: ArrayList<Song>
        var songPosition: Int  = 0
        //        var mediaPlayer: MediaPlayer? = null
        var isPlaying: Boolean = false
        var musicService: MusicService? = null
        @SuppressLint("StaticFieldLeak")
        lateinit var binding: ActivityPlayerBinding
        var nowPlayingId: String = ""
    }




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)



        initializeLayout()

        binding.playPause.setOnClickListener{
            if (isPlaying)
                pauseMusic()
            else
                playMusic()
        }

        binding.previous.setOnClickListener{
            previousOrNextSong(false)
        }

        binding.next.setOnClickListener{
            previousOrNextSong(true)
        }

        binding.seekBarPA.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            private var isUserTouching = false

            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    isUserTouching = true
                    // Обновляем только текстовое отображение текущего времени
                    binding.start.text = formatDuration(progress.toLong())
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
                isUserTouching = true
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                isUserTouching = false
                // Выполняем seekTo только после завершения перетаскивания
                seekBar?.progress?.let {
                    musicService?.mediaPlayer?.seekTo(it)
                }
            }
        })


        binding.back.setOnClickListener{
            finish()
        }

//        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
//            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
//            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
//            insets
//        }


    }

    private fun initializeLayout(){
        val song_class = intent.getStringExtra("song_class")
        songPosition = intent.getIntExtra("position", 0)

        when(song_class){
            "MyMusic" -> {
                val intent = Intent(this, MusicService::class.java)
                bindService(intent, this, BIND_AUTO_CREATE)
                startService(intent)
                musicListPA = ArrayList()
                musicListPA.addAll(MainActivity.musicListMA)
                setLayout()
                binding.namePlaylist.text = "Мои треки"
            }
            "MiniPlayer" -> {
                setLayout()
                binding.start.text = formatDuration(musicService!!.mediaPlayer!!.currentPosition.toLong())
                binding.duration.text = formatDuration(musicService!!.mediaPlayer!!.duration.toLong())
                binding.seekBarPA.progress = musicService!!.mediaPlayer!!.currentPosition
                binding.seekBarPA.max = musicService!!.mediaPlayer!!.duration
                if (!isPlaying)
                    binding.playPause.setIconResource(R.drawable.baseline_play_arrow_24)

            }
        }
    }


    private fun setLayout(){
        Glide.with(this)
            .load(musicListPA[songPosition].artUri)
            .apply(RequestOptions().placeholder(R.drawable.baseline_music_off_24).centerCrop())
            //.apply(RequestOptions().transform(RoundedCorners(32)))
            .into(binding.songImg)


        binding.title.text = musicListPA[songPosition].title
        binding.artist.text = musicListPA[songPosition].artist

    }


    private fun createMediaPlayer(){
        try {
            if (musicService!!.mediaPlayer == null)
                musicService!!.mediaPlayer = MediaPlayer()

            musicService!!.mediaPlayer!!.reset()
            musicService!!.mediaPlayer!!.setDataSource(musicListPA[songPosition].path)
            musicService!!.mediaPlayer!!.prepare()
            musicService!!.mediaPlayer!!.start()
            isPlaying = true
            binding.playPause.setIconResource(R.drawable.baseline_pause_24)
            binding.start.text = formatDuration(musicService!!.mediaPlayer!!.currentPosition.toLong())
            binding.duration.text = formatDuration(musicService!!.mediaPlayer!!.duration.toLong())
            binding.seekBarPA.progress = 0
            binding.seekBarPA.max = musicService!!.mediaPlayer!!.duration

            musicService!!.mediaPlayer!!.setOnCompletionListener(this)

            nowPlayingId = musicListPA[songPosition].id
        }catch (e: Exception) {return}
    }

    private fun playMusic(){
        binding.playPause.setIconResource(R.drawable.baseline_pause_24)
        isPlaying = true
        musicService!!.mediaPlayer!!.start()
    }

    private fun pauseMusic(){
        binding.playPause.setIconResource(R.drawable.baseline_play_arrow_24)
        isPlaying = false
        musicService!!.mediaPlayer!!.pause()
    }

    private fun previousOrNextSong(increment: Boolean){
        if(increment) {
            setSongPosition(increment = true)
            setLayout()
            createMediaPlayer()
        }
        else {
            setSongPosition(increment = false)
            setLayout()
            createMediaPlayer()
        }
    }


    override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
        val binder = service as MusicService.MyBinder
        musicService = binder.currentService()
        createMediaPlayer()
        musicService!!.seekBarSetup()

    }

    override fun onServiceDisconnected(p0: ComponentName?) {
        musicService = null
    }

    override fun onCompletion(p0: MediaPlayer?) {
        setSongPosition(increment = true)
        createMediaPlayer()
        try{
            setLayout()
        }catch (e: Exception){
            return}
    }
}
