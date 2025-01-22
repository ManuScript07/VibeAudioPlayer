package com.example.vibe_audio_player

import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import com.example.vibe_audio_player.databinding.ActivityMainBinding
import java.io.File


class MainActivity : AppCompatActivity() {

    private lateinit var binding : ActivityMainBinding
    companion object{
        lateinit var musicListMA: ArrayList<Song>
    }
    @RequiresApi(Build.VERSION_CODES.R)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestRuntimePermission()
        binding = ActivityMainBinding.inflate(layoutInflater)


        setContentView(binding.root)



        replaceFragment(MyMusic())
        musicListMA = loadTracks()
        binding.bottomNavigationView.setOnItemSelectedListener {
            when(it.itemId){
                R.id.my_music -> replaceFragment(MyMusic())
                R.id.for_you -> replaceFragment(ForYou())
                R.id.stream -> replaceFragment(Stream())
                R.id.search -> replaceFragment(Search())
                R.id.profile -> replaceFragment(Profile())

                else -> {

                }
            }
            true
        }

    }

    // Заменяем фрагмент
    private fun replaceFragment(fragment: Fragment) {
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.frame_layout, fragment)
        fragmentTransaction.commit()
    }


    // Запрашиваем разрешения
    private fun requestRuntimePermission() : Boolean {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
            if (ActivityCompat.checkSelfPermission(
                    this,
                    android.Manifest.permission.READ_EXTERNAL_STORAGE
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE),
                    13
                )
                return false
            }
        } else {
            // Android 13 or Higher permission request
            if (ActivityCompat.checkSelfPermission(
                    this,
                    android.Manifest.permission.READ_MEDIA_AUDIO
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(android.Manifest.permission.READ_MEDIA_AUDIO),
                    13
                )
                return false
            }
        }
        return true
    }


    @RequiresApi(Build.VERSION_CODES.R)
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 13) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //replaceFragment(MyMusic()) // Чтобы треки появились
//                Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show()
            } else {
//                Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show()
                // Повторный запрос разрешения (опционально)
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(android.Manifest.permission.READ_MEDIA_AUDIO),
                    13
                )
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

        val cursor = this?.contentResolver?.query(
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
            projection,
            selection,
            null,
            "${MediaStore.Audio.Media.DATE_ADDED} DESC"
        )

        cursor?.use { c ->
            // Получаем индексы колонок заранее
            val idIndex = c.getColumnIndexOrThrow(MediaStore.Audio.Media._ID)
            val titleIndex = c.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE)
            val albumIndex = c.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM)
            val artistIndex = c.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST)
            val durationIndex = c.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION)
            val dataIndex = c.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA)
            val albumIdIndex = c.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ID)

            while (c.moveToNext()) {
                // Извлекаем данные по индексам
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

                // Проверяем существование файла
                if (File(song.path).exists()) {
                    tempList.add(song)
                }
            }
        }

        return tempList
    }

}
