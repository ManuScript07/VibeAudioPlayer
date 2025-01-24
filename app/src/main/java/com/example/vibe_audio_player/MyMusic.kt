package com.example.vibe_audio_player

import SongRVAdapter
import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.vibe_audio_player.databinding.FragmentMymusicBinding
import com.example.vibe_audio_player.databinding.SongItemBinding

class MyMusic : Fragment() {

    private lateinit var binding: FragmentMymusicBinding
    private lateinit var bindingitem: SongItemBinding

    companion object{
        lateinit var musicListMM: ArrayList<Song>
    }

    private var isClickAllowed = true

    @RequiresApi(Build.VERSION_CODES.R)
    @SuppressLint("SetTextI18n", "ResourceAsColor")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMymusicBinding.inflate(inflater, container, false)

        musicListMM = ArrayList()
        musicListMM.addAll(MainActivity.musicListMA)

        // Убедимся, что переключение ViewSwitcher работает корректно
        if (musicListMM.isEmpty()) {

            binding.viewSwitcher.displayedChild = 1 // Показываем сообщение и кнопку
            binding.addMusicButton.setOnClickListener {

            }
        } else {

            binding.viewSwitcher.displayedChild = 0 // Показываем RecyclerView
            binding.recyclerView.setHasFixedSize(true)
            binding.recyclerView.setItemViewCacheSize(13)
            binding.recyclerView.layoutManager = LinearLayoutManager(context)
            binding.recyclerView.adapter = context?.let {
                val visibleTracks = if (musicListMM.size > 4) musicListMM.subList(0, 4) else musicListMM
                SongRVAdapter(it, visibleTracks) { song, position ->
                    if (isClickAllowed) {
                        isClickAllowed = false
                        val intent = Intent(context, PlayerActivity::class.java).apply {
                            if (musicListMM[position].id == PlayerActivity.nowPlayingId)
                                putExtra("song_class", "MiniPlayer")
                            else
                                putExtra("song_class", "MyMusic")
                            putExtra("position", position)
                        }
                        startActivity(intent)
                        binding.root.postDelayed({ isClickAllowed = true }, 500)
                    }
                }
            }
        }
        binding.textView2.text = musicListMM.size.toString()

        binding.playList.setOnClickListener{

        }

        return binding.root
    }
    
}
