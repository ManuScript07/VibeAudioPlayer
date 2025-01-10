package com.example.vibe_audio_player

import android.media.MediaPlayer
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.vibe_audio_player.databinding.ActivityPlayerBinding

class PlayerActivity : AppCompatActivity() {

    companion object{
        lateinit var musicListPA: ArrayList<Song>
        var songPosition: Int  = 0
        var mediaPlayer: MediaPlayer? = null
        var isPlaying: Boolean = false
    }

    private lateinit var binding: ActivityPlayerBinding


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
                musicListPA = ArrayList()
                musicListPA.addAll(MyMusic.MusicListMM)
                setLayout()
                binding.namePlaylist.text = "Мои треки"
                createMediaPlayer()
            }
        }
    }


    private fun setLayout(){
        Glide.with(this)
            .load(musicListPA[songPosition].artUri)
            .apply(RequestOptions().placeholder(R.drawable.baseline_music_off_24).centerCrop())
            .into(binding.songImg)


        binding.title.text = musicListPA[songPosition].title
        binding.artist.text = musicListPA[songPosition].artist
        val duration: Long = musicListPA[songPosition].duration
        binding.duration.text = String.format("%02d:%02d", duration / 1000 / 60, duration / 1000 % 60)
    }


    private fun createMediaPlayer(){
        try {
            if (mediaPlayer == null)
                mediaPlayer = MediaPlayer()

            mediaPlayer!!.reset()
            mediaPlayer!!.setDataSource(musicListPA[songPosition].path)
            mediaPlayer!!.prepare()
            mediaPlayer!!.start()
            isPlaying = true
            binding.playPause.setIconResource(R.drawable.baseline_pause_24)
        }catch (e: Exception) {return}
    }

    private fun playMusic(){
        binding.playPause.setIconResource(R.drawable.baseline_pause_24)
        isPlaying = true
        mediaPlayer!!.start()
    }

    private fun pauseMusic(){
        binding.playPause.setIconResource(R.drawable.baseline_play_arrow_24)
        isPlaying = false
        mediaPlayer!!.pause()
    }

    private fun previousOrNextSong(increment: Boolean){
        val length: Int = musicListPA.size
        if(increment) {
            songPosition = (songPosition+1)%length
            setLayout()
            createMediaPlayer()
        }
        else {
            if (songPosition == 0)
                songPosition = length-1
            else
                --songPosition
            setLayout()
            createMediaPlayer()
        }
    }

}