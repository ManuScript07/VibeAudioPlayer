package com.example.vibe_audio_player.activities

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.example.vibe_audio_player.Song
import com.example.vibe_audio_player.databinding.ActivityMainBinding
import com.example.vibe_audio_player.fragments.MainFragment.Companion.musicListMF
import java.io.File


class MainActivity : AppCompatActivity() {

    companion object {
//        var musicListMA: ArrayList<Song> = ArrayList()
        @SuppressLint("StaticFieldLeak")
        lateinit var binding: ActivityMainBinding
        var permission: Boolean = false

        @SuppressLint("Recycle", "Range")
        @RequiresApi(Build.VERSION_CODES.R)
        fun loadTracks(context: Context): ArrayList<Song> {
            val tempList = ArrayList<Song>()

            val selection = "${MediaStore.Audio.Media.IS_MUSIC}!=0"
            val projection = arrayOf(
                MediaStore.Audio.Media._ID,
                MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media.ALBUM,
                MediaStore.Audio.Media.ARTIST,
                MediaStore.Audio.Media.DURATION,
                MediaStore.Audio.Media.DATE_ADDED,
                MediaStore.Audio.Media.SIZE,
                MediaStore.Audio.Media.DATA,
                MediaStore.Audio.Media.ALBUM_ID,
            )

            val cursor = context.contentResolver?.query(
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
                val dateAddedIndex = c.getColumnIndexOrThrow(MediaStore.Audio.Media.DATE_ADDED)
                val sizeIndex = c.getColumnIndexOrThrow(MediaStore.Audio.Media.SIZE)
                val dataIndex = c.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA)
                val albumIdIndex = c.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ID)

                while (c.moveToNext()) {
                    val id = c.getString(idIndex)
                    val title = c.getString(titleIndex)
                    val album = c.getString(albumIndex)
                    val artist = c.getString(artistIndex)
                    val duration = c.getLong(durationIndex)
                    val dateAdded = c.getLong(dateAddedIndex)
                    val size = c.getLong(sizeIndex)
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
                        dateAdded = dateAdded,
                        size = size,
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

    @RequiresApi(Build.VERSION_CODES.R)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

//        musicListMA = ArrayList()
        permission = requestRuntimePermission()

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
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == 13) {
            val allGranted = grantResults.all { it == PackageManager.PERMISSION_GRANTED }
            if (allGranted) {
                musicListMF = loadTracks(this)
            } else {
                Toast.makeText(this, "Разрешения не предоставлены", Toast.LENGTH_SHORT).show()
            }
        }
    }


}

