package com.example.vibe_audio_player.activities

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.vibe_audio_player.R
import com.example.vibe_audio_player.Song
import com.example.vibe_audio_player.databinding.ActivityMainBinding
import com.example.vibe_audio_player.fragments.PlayerFragment
import com.example.vibe_audio_player.fragments.PlayerFragment.Companion.musicListPF
import com.example.vibe_audio_player.fragments.PlayerFragment.Companion.songPosition
import com.example.vibe_audio_player.fragments.PlayerFragmentDirections
import com.example.vibe_audio_player.setSongPosition
import java.io.File


class MainActivity : AppCompatActivity() {



//    private lateinit var resultLauncher: ActivityResultLauncher<Intent>
    companion object {
        var musicListMA: ArrayList<Song> = ArrayList()
        @SuppressLint("StaticFieldLeak")
        lateinit var binding: ActivityMainBinding
    }

    @RequiresApi(Build.VERSION_CODES.R)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


        binding.playPause.setOnClickListener {
            if (PlayerFragment.isPlaying)
                pauseMusic()
            else
                playMusic()
        }


        binding.next.setOnClickListener {
            setSongPosition(increment = true)
            PlayerFragment.musicService!!.createMediaPlayer()
            Glide.with(this)
                .load(musicListPF[songPosition].artUri)
                .apply(RequestOptions().placeholder(R.drawable.baseline_music_off_24).centerCrop())
                .into(binding.image)

            binding.songName.text = musicListPF[songPosition].title
            binding.artistName.text = musicListPF[songPosition].artist
            playMusic()
        }

        binding.miniPlayer.setOnClickListener {
            val action = PlayerFragmentDirections.actionGlobalPlayerFragment(
                SONGCLASS = "MiniPlayer",
                SONGPOSITION = songPosition,
                NAMEPLAYLIST = "Мои треки"
            )
            findNavController(R.id.nav_host_fragment).navigate(action)
        }


        val controller = findNavController(R.id.nav_host_fragment)
        binding.bottomNavigationView.setupWithNavController(controller)
            controller.addOnDestinationChangedListener { _, destination, _ ->
                when (destination.id) {
                    R.id.playerFragment -> {
                        binding.bottomNavigationView.visibility = View.GONE
                        binding.bottomNavigationView.animate()
                            .translationY(binding.bottomNavigationView.height.toFloat())
                            .setDuration(300)
                            .start()
                    }
                    else -> {
                        binding.bottomNavigationView.visibility = View.VISIBLE
                        binding.bottomNavigationView.animate()
                            .translationY(0f)
                            .setDuration(300)
                            .start()
                    }
                }
            }

        musicListMA = ArrayList()

        if (requestRuntimePermission()) {
            loadMusicList()
        }
    }

    override fun onResume() {
        super.onResume()


        if (PlayerFragment.musicService != null) {
            binding.miniPlayer.visibility = View.VISIBLE
            binding.songName.isSelected = true
            Glide.with(this)
                .load(musicListPF[songPosition].artUri)
                .apply(RequestOptions().placeholder(R.drawable.baseline_music_off_24).centerCrop())
                .into(binding.image)
            binding.songName.text = musicListPF[songPosition].title
            binding.artistName.text = musicListPF[songPosition].artist
            if (PlayerFragment.isPlaying)
                binding.playPause.setImageResource(R.drawable.baseline_pause_32)

            else
                binding.playPause.setImageResource(R.drawable.baseline_play_arrow_32)
        }
    }


    private fun playMusic(){
        PlayerFragment.isPlaying = true
        PlayerFragment.musicService!!.mediaPlayer!!.start()
        binding.playPause.setImageResource(R.drawable.baseline_pause_32)
        PlayerFragment.binding.next.setImageResource(R.drawable.baseline_pause_48)

    }

    private fun pauseMusic(){
        PlayerFragment.isPlaying = false
        PlayerFragment.musicService!!.mediaPlayer!!.pause()
        binding.playPause.setImageResource(R.drawable.baseline_play_arrow_32)
        PlayerFragment.binding.next.setImageResource(R.drawable.baseline_play_arrow_48)

    }





    // Метод для проверки и запроса разрешений
    private fun requestRuntimePermission(): Boolean {
        val permissions = if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
            arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)
        } else {
            arrayOf(Manifest.permission.READ_MEDIA_AUDIO)
        }

        val isGranted = permissions.all {
            ActivityCompat.checkSelfPermission(this, it) == PackageManager.PERMISSION_GRANTED
        }

        if (!isGranted) {
            ActivityCompat.requestPermissions(this, permissions, 13)
        }

        return isGranted
    }

    @RequiresApi(Build.VERSION_CODES.R)
    private fun loadMusicList() {
        musicListMA = loadTracks()
    }

    @RequiresApi(Build.VERSION_CODES.R)
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == 13) {
            val allGranted = grantResults.all { it == PackageManager.PERMISSION_GRANTED }
            if (allGranted) {
                loadMusicList()
            } else {
                Toast.makeText(this, "Разрешения не предоставлены", Toast.LENGTH_SHORT).show()
            }
        }
    }

    @SuppressLint("Recycle", "Range")
    @RequiresApi(Build.VERSION_CODES.R)
    private fun loadTracks(): ArrayList<Song> {
        val tempList = ArrayList<Song>()

        val selection = "${MediaStore.Audio.Media.IS_MUSIC}!=0"
        val projection = arrayOf(
            MediaStore.Audio.Media._ID,
            MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.ALBUM,
            MediaStore.Audio.Media.ARTIST,
            MediaStore.Audio.Media.DURATION,
            MediaStore.Audio.Media.DATE_ADDED,
            MediaStore.Audio.Media.DATA,
            MediaStore.Audio.Media.ALBUM_ID,
        )

        val cursor = contentResolver?.query(
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
            projection,
            selection,
            null,
            "${MediaStore.Audio.Media.DATE_ADDED} DESC"
        )

        cursor?.use { c ->
            val idIndex = c.getColumnIndexOrThrow(MediaStore.Audio.Media._ID)
            val titleIndex = c.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE)
            val albumIndex = c.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM)
            val artistIndex = c.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST)
            val durationIndex = c.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION)
            val dataIndex = c.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA)
            val albumIdIndex = c.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ID)

            while (c.moveToNext()) {
                val id = c.getString(idIndex)
                val title = c.getString(titleIndex)
                val album = c.getString(albumIndex)
                val artist = c.getString(artistIndex)
                val duration = c.getLong(durationIndex)
                val path = c.getString(dataIndex)
                val albumId = c.getLong(albumIdIndex).toString()
                val uri = Uri.parse("content://media/external/audio/albumart")
                val artUri = Uri.withAppendedPath(uri, albumId).toString()

                val song = Song(
                    id = id,
                    title = title,
                    album = album,
                    artist = artist,
                    duration = duration,
                    path = path,
                    artUri = artUri
                )

                if (File(song.path).exists()) {
                    tempList.add(song)
                }
            }
        }

        return tempList
    }
}

