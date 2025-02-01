package com.example.vibe_audio_player.fragments

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.vibe_audio_player.R
import com.example.vibe_audio_player.Song
import com.example.vibe_audio_player.activities.MainActivity
import com.example.vibe_audio_player.adapters.SongRVAdapter
import com.example.vibe_audio_player.databinding.FragmentMymusicBinding
import com.example.vibe_audio_player.fragments.MainFragment.Companion.musicListMF

class MyMusic : Fragment() {

    private lateinit var binding: FragmentMymusicBinding
    private lateinit var adapter: SongRVAdapter

    companion object{
        var musicListMM: ArrayList<Song> = ArrayList()
        lateinit var adapter: SongRVAdapter
    }

    private var isClickAllowed = true

    @RequiresApi(Build.VERSION_CODES.R)
    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMymusicBinding.inflate(inflater, container, false)

        adapter = SongRVAdapter(requireContext(), musicListMM) { song, position ->
            openPlayerFragment(position)
        }


        binding.recyclerView.apply {
            setHasFixedSize(true)
            setItemViewCacheSize(13)
            layoutManager = LinearLayoutManager(context)
            adapter = this@MyMusic.adapter
        }


        binding.addMusicButton.setOnClickListener {
            updateSongs()
        }

        binding.playList.setOnClickListener {
            findNavController().navigate(R.id.action_my_music_to_playListFragment)
        }

        binding.goToAllSongs.setOnClickListener{
            findNavController().navigate(R.id.action_my_music_to_myTracksFragment)
        }

        binding.button2.setOnClickListener {
            updateSongs()
        }

        return binding.root
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.countSongs.text = musicListMF.size.toString()
    }


    @SuppressLint("NotifyDataSetChanged", "SetTextI18n")
    @RequiresApi(Build.VERSION_CODES.R)
    override fun onResume() {
        super.onResume()
        binding.countSongs.text = musicListMF.size.toString()
        if (adapter.itemCount == 0)
            updateSongs()
    }

    // Функция для открытия PlayerActivity
    private fun openPlayerFragment(position: Int) {
        if (isClickAllowed) {
            isClickAllowed = false
            val action = PlayerFragmentDirections.actionGlobalPlayerFragment(
                SONGCLASS = (if (musicListMM[position].id == PlayerFragment.nowPlayingId) "MiniPlayer" else "MyMusic"),
                SONGPOSITION = position,
                NAMEPLAYLIST = "Мои треки"
            )
            findNavController().navigate(action)

            binding.root.postDelayed({ isClickAllowed = true }, 500)
        }
    }

    @SuppressLint("SetTextI18n")
    @RequiresApi(Build.VERSION_CODES.R)
    private fun updateSongs(){
        musicListMF = MainActivity.loadTracks(requireContext())
        val updateList = ArrayList(musicListMF.take(4))
        if (PlayerFragment.musicService != null) {
            PlayerFragment.musicListPF.clear()
            PlayerFragment.musicListPF.addAll(musicListMF)
        }

        // Проверяем, пустой ли список
        if (updateList.isEmpty()) {
            binding.viewSwitcher.displayedChild = 1 // Показываем сообщение
        } else {
            binding.viewSwitcher.displayedChild = 0 // Показываем RecyclerView
            adapter.updateData(updateList)
            binding.countSongs.text = musicListMF.size.toString()
        }


    }
}
