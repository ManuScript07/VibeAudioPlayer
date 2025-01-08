package com.example.vibe_audio_player

import SongRVAdapter
import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.vibe_audio_player.databinding.FragmentMymusicBinding
import java.io.File

class MyMusic : Fragment() {

    private lateinit var binding: FragmentMymusicBinding

    companion object{
        lateinit var MusicListSA: ArrayList<Song>
    }


    @RequiresApi(Build.VERSION_CODES.R)
    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMymusicBinding.inflate(inflater, container, false)

        MusicListSA = loadTracks()

        // Убедимся, что переключение ViewSwitcher работает корректно
        if (MusicListSA.isEmpty()) {

            binding.viewSwitcher.displayedChild = 1 // Показываем сообщение и кнопку
            binding.addMusicButton.setOnClickListener {

            }
        } else {

            binding.viewSwitcher.displayedChild = 0 // Показываем RecyclerView
            binding.recyclerView.setHasFixedSize(true)
            binding.recyclerView.setItemViewCacheSize(13)
            binding.recyclerView.layoutManager = LinearLayoutManager(context)
            binding.recyclerView.adapter = context?.let {
                SongRVAdapter(it, MusicListSA) { song ->
                    //Toast.makeText(context, "Вы выбрали: ${song.title}", Toast.LENGTH_SHORT).show()
                    val intent = Intent(context, PlayerActivity::class.java).apply {
                        putExtra("song_id", song.id)
                        putExtra("song_title", song.title)
                        putExtra("song_artist", song.artist)
                        putExtra("song_album", song.album)
                        putExtra("song_duration", song.duration)
                        putExtra("song_path", song.path)
                    }
                    startActivity(intent)
                }
            }
        }
        binding.textView2.text = MusicListSA.size.toString()


        return binding.root
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
            MediaStore.Audio.Media.ALBUM_ID
        )

        val cursor = context?.contentResolver?.query(
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
