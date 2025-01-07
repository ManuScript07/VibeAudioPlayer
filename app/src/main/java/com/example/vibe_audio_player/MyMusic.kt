package com.example.vibe_audio_player

import SongRVAdapter
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.vibe_audio_player.databinding.FragmentMymusicBinding

class MyMusic : Fragment() {

    private lateinit var binding: FragmentMymusicBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMymusicBinding.inflate(inflater, container, false)

        val tracks = loadTracks()

        // Убедимся, что переключение ViewSwitcher работает корректно
        if (tracks.isEmpty()) {

            binding.viewSwitcher.displayedChild = 1 // Показываем сообщение и кнопку
            binding.addMusicButton.setOnClickListener {

            }
        } else {

            binding.viewSwitcher.displayedChild = 0 // Показываем RecyclerView
            binding.recyclerView.layoutManager = LinearLayoutManager(context)
            binding.recyclerView.adapter = SongRVAdapter(tracks) { song ->
                Toast.makeText(context, "Вы выбрали: ${song.name}", Toast.LENGTH_SHORT).show()
            }
        }

        return binding.root
    }

    private fun loadTracks(): List<Song> {
        // Верните пустой или заполненный список для проверки
        // В экран помещается 5 !!!
        return listOf(
             Song("Название песни 1", "Исполнитель 1", "ic_launcher_foreground.xml"),
             Song("Название песни 2", "Исполнитель 2", "ic_launcher_foreground.xml"),
             Song("Название песни 3", "Исполнитель 3", "ic_launcher_foreground.xml"),
             Song("Название песни 4", "Исполнитель 4", "ic_launcher_foreground.xml"),
             Song("Название песни 5", "Исполнитель 5", "ic_launcher_foreground.xml"),
        )
    }
}
