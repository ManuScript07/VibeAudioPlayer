package com.example.vibe_audio_player.activities

import android.annotation.SuppressLint
import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.GradientDrawable
import android.media.MediaMetadataRetriever
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.os.IBinder
import android.provider.MediaStore
import android.widget.SeekBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.vibe_audio_player.R
import com.example.vibe_audio_player.Song
import com.example.vibe_audio_player.databinding.ActivityPlayerBinding
import com.example.vibe_audio_player.formatDuration
import com.example.vibe_audio_player.fragments.MiniPlayer
import com.example.vibe_audio_player.fragments.PlayerFragment.Companion.musicService
import com.example.vibe_audio_player.services.MusicService
import com.example.vibe_audio_player.setSongPosition
import kotlin.system.exitProcess


class PlayerActivity : AppCompatActivity(), ServiceConnection, MediaPlayer.OnCompletionListener {

    companion object{
        lateinit var musicListPA: ArrayList<Song>
        var songPosition: Int  = 0
        //        var mediaPlayer: MediaPlayer? = null
        var isPlaying: Boolean = false
//        var musicService: MusicService? = null
        @SuppressLint("StaticFieldLeak")
        lateinit var binding: ActivityPlayerBinding
        var nowPlayingId: String = ""
        var namePlayList: String = ""
    }




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)


        if(intent.data?.scheme.contentEquals("content")){
            songPosition = 0
            val intentService = Intent(this, MusicService::class.java)
            bindService(intentService, this, BIND_AUTO_CREATE)
            startService(intentService)
            musicListPA = ArrayList()
            musicListPA.add(getMusicDetails(intent.data!!))
            Glide.with(this)
                .load(getImgArt(musicListPA[songPosition].path))
                .apply(RequestOptions().placeholder(R.drawable.baseline_music_off_24))
                .into(binding.songImg)
            binding.title.text = musicListPA[songPosition].title
            binding.artist.text = musicListPA[songPosition].artist
        }else initializeLayout()


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
                    //musicService?.mediaPlayer?.seekTo(it)
                }
            }
        })
        binding.back.setOnClickListener{
            finish()
        }
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
                namePlayList = "Мои треки"
                setLayout()

            }
            "MiniPlayer" -> {
                setLayout()
//                binding.start.text = formatDuration(musicService!!.mediaPlayer!!.currentPosition.toLong())
//                binding.duration.text = formatDuration(musicService!!.mediaPlayer!!.duration.toLong())
//                binding.seekBarPA.progress = musicService!!.mediaPlayer!!.currentPosition
//                binding.seekBarPA.max = musicService!!.mediaPlayer!!.duration
                if (!isPlaying)
                    binding.playPause.setImageResource(R.drawable.baseline_play_arrow_24)
            }
        }
        binding.namePlaylist.text =
            if (intent.getStringExtra("namePlayList") == "")
                namePlayList
            else
                intent.getStringExtra("namePlayList")

        if (musicService != null && !isPlaying)
            playMusic()
    }


    private fun setLayout(){
        Glide.with(applicationContext)
            .load(musicListPA[songPosition].artUri)
            .apply(RequestOptions().placeholder(R.drawable.baseline_music_off_24).centerCrop())
            .into(binding.songImg)


        binding.title.text = musicListPA[songPosition].title
        binding.artist.text = musicListPA[songPosition].artist


        val img = getImgArt(musicListPA[songPosition].path)
        val image = if (img != null){
            BitmapFactory.decodeByteArray(
                img, 0, img.size
            )
        }else {
            BitmapFactory.decodeResource(
                resources, R.drawable.baseline_music_off_24
            )
        }

        val bgColor = getMainColor(image)
        val gradient = GradientDrawable(
            GradientDrawable.Orientation.BOTTOM_TOP,
            intArrayOf(0xFFFFFF, bgColor)
        )
        binding.root.background = gradient
        window?.statusBarColor = bgColor

    }




    private fun createMediaPlayer(){
        try {
//            if (musicService!!.mediaPlayer == null) musicService!!.mediaPlayer = MediaPlayer()
//            musicService!!.mediaPlayer!!.reset()
//            musicService!!.mediaPlayer!!.setDataSource(musicListPA[songPosition].path)
//            musicService!!.mediaPlayer!!.prepare()
//            binding.start.text = formatDuration(musicService!!.mediaPlayer!!.currentPosition.toLong())
//            binding.duration.text = formatDuration(musicService!!.mediaPlayer!!.duration.toLong())
//            binding.seekBarPA.progress = 0
//            binding.seekBarPA.max = musicService!!.mediaPlayer!!.duration
//            musicService!!.mediaPlayer!!.setOnCompletionListener(this)
//            nowPlayingId = musicListPA[songPosition].id
//            playMusic()

        }catch (e: Exception){Toast.makeText(this, e.toString(), Toast.LENGTH_LONG).show()}
    }

    private fun playMusic(){
        binding.playPause.setImageResource(R.drawable.baseline_pause_24)
        isPlaying = true
//        musicService!!.mediaPlayer!!.start()
    }

    private fun pauseMusic(){
        binding.playPause.setImageResource(R.drawable.baseline_play_arrow_24)
        isPlaying = false
//        musicService!!.mediaPlayer!!.pause()
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

    private fun getImgArt(path: String): ByteArray?{
        val retriever = MediaMetadataRetriever()
        retriever.setDataSource(path)
        return retriever.embeddedPicture
    }

    private fun getMusicDetails(contentUri: Uri): Song {
        var cursor: Cursor? = null
        try {
            val projection = arrayOf(MediaStore.Audio.Media.DATA, MediaStore.Audio.Media.DURATION)
            cursor = this.contentResolver.query(contentUri, projection, null, null, null)
            val dataColumn = cursor?.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA)
            val durationColumn = cursor?.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION)
            cursor!!.moveToFirst()
            val path = dataColumn?.let { cursor.getString(it) }
            val duration = durationColumn?.let { cursor.getLong(it) }!!
            return Song(id = "Unknown", title = path.toString(), album = "Unknown", artist = "Unknown", duration = duration,
                artUri = "Unknown", path = path.toString())
        }finally {
            cursor?.close()
        }
    }


    private fun getMainColor(img: Bitmap): Int {
        val newImg = Bitmap.createScaledBitmap(img, 1, 1, true)
        val color = newImg.getPixel(0, 0)
        newImg.recycle()
        return color
    }


    override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
        if(musicService == null){
            val binder = service as MusicService.MyBinder
            musicService = binder.currentService()
//            musicService!!.audioManager = getSystemService(AUDIO_SERVICE) as AudioManager
//            musicService!!.audioManager.requestAudioFocus(musicService, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN)
        }
        createMediaPlayer()
        musicService!!.seekBarSetup()


    }

    override fun onServiceDisconnected(p0: ComponentName?) {
        musicService = null
    }

    override fun onCompletion(mp: MediaPlayer?) {
        setSongPosition(increment = true)
        createMediaPlayer()
        setLayout()

        MiniPlayer.binding.songName.isSelected = true
        Glide.with(applicationContext)
            .load(musicListPA[songPosition].artUri)
            .apply(RequestOptions().placeholder(R.drawable.baseline_music_off_24).centerCrop())
            .into(MiniPlayer.binding.image)
        MiniPlayer.binding.songName.text = musicListPA[songPosition].title
    }

    override fun onDestroy() {
        super.onDestroy()
        if(musicListPA[songPosition].id == "Unknown" && !isPlaying) exitApplication()
    }

    fun exitApplication() {
        if (musicService != null) {
            //PlayerActivity.musicService!!.audioManager.abandonAudioFocus(PlayerActivity.musicService)
            musicService!!.stopForeground(true)
//            musicService!!.mediaPlayer!!.release()
            musicService = null
        }
        exitProcess(1)
    }


}

