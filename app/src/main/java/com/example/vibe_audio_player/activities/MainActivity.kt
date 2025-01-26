package com.example.vibe_audio_player.activities

import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.example.vibe_audio_player.R
import com.example.vibe_audio_player.Song
import com.example.vibe_audio_player.databinding.ActivityMainBinding
import java.io.File


class MainActivity : AppCompatActivity() {


    private lateinit var binding: ActivityMainBinding

    companion object {
        var musicListMA: ArrayList<Song> = ArrayList()
    }

    @RequiresApi(Build.VERSION_CODES.R)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val controller = findNavController(R.id.nav_host_fragment)
        binding.bottomNavigationView.setupWithNavController(controller)

        // Инициализация списка треков
        musicListMA = ArrayList()

        if (requestRuntimePermission()) {
            loadMusicList()
        }
    }

    // Метод для проверки и запроса разрешений
    private fun requestRuntimePermission(): Boolean {
        val permissions = if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
            arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE)
        } else {
            arrayOf(android.Manifest.permission.READ_MEDIA_AUDIO)
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

