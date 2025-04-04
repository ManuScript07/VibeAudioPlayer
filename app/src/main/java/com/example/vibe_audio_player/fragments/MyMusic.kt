package com.example.vibe_audio_player.fragments

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.vibe_audio_player.R
import com.example.vibe_audio_player.Song
import com.example.vibe_audio_player.SongUtils.sortSongs
import com.example.vibe_audio_player.activities.MainActivity
import com.example.vibe_audio_player.adapters.SongRVAdapter
import com.example.vibe_audio_player.databinding.FragmentMymusicBinding
import com.example.vibe_audio_player.fragments.MainFragment.Companion.musicListMF
import com.example.vibe_audio_player.fragments.MyTracksFragment.Companion.isSearch
import com.example.vibe_audio_player.fragments.MyTracksFragment.Companion.isShuffle
import com.example.vibe_audio_player.fragments.PlayerFragment.Companion.songPosition
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MyMusic : Fragment() {

    private lateinit var binding: FragmentMymusicBinding
    private lateinit var adapter: SongRVAdapter

    var musicListMM: ArrayList<Song> = ArrayList()
    private var isClickAllowed = true

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMymusicBinding.inflate(inflater, container, false)
        return binding.root
    }


    @RequiresApi(Build.VERSION_CODES.R)
    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = SongRVAdapter(requireContext(), musicListMM) { song, position ->
            openPlayerFragment(position)
        }
        updateSongs()
        binding.recyclerView.apply {
            setHasFixedSize(true)
            setItemViewCacheSize(13)
            layoutManager = LinearLayoutManager(context)
            adapter = this@MyMusic.adapter
        }


        binding.addMusicButton.setOnClickListener {
            updateSongs(updateLoadSongs = true)
        }

        binding.playList.setOnClickListener {
            findNavController().navigate(R.id.action_my_music_to_playListFragment)
        }

        binding.goToAllSongs.setOnClickListener{
            findNavController().navigate(R.id.action_my_music_to_myTracksFragment)
        }


        binding.swipeRefreshLayout.setColorSchemeResources(
            R.color.purple_500
        )

        binding.swipeRefreshLayout.setOnRefreshListener {
            refreshData()
        }

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

    private fun openPlayerFragment(position: Int) {
        if (isClickAllowed) {
            isClickAllowed = false

            val action = PlayerFragmentDirections.actionGlobalPlayerFragment(
                SONGCLASS = (if (musicListMM[position].id == PlayerFragment.nowPlayingId) "MiniPlayer" else "MyMusic"),
                SONGPOSITION = (if (musicListMM[ position].id == PlayerFragment.nowPlayingId && (isShuffle || isSearch)) songPosition else position),
                NAMEPLAYLIST = (if (musicListMM[position].id == PlayerFragment.nowPlayingId && isShuffle) "Перемешанное" else "Мои треки")
            )
            if (musicListMM[position].id != PlayerFragment.nowPlayingId && isShuffle)
                isShuffle = false
            findNavController().navigate(action)

            binding.root.postDelayed({ isClickAllowed = true }, 500)
        }
    }

    @SuppressLint("SetTextI18n")
    @RequiresApi(Build.VERSION_CODES.R)
    private fun updateSongs(updateLoadSongs: Boolean = false){
        if (updateLoadSongs) {
            musicListMF = MainActivity.loadTracks(requireContext())
            if (PlayerFragment.musicService != null) {
                val sharedPrefs = requireContext().getSharedPreferences("SortPreferences", Context.MODE_PRIVATE)
                val selectedSortOption = sharedPrefs.getString("SORT_OPTION", "Название песни") ?: "Название песни"
                val isAscending = sharedPrefs.getBoolean("SORT_ORDER", true)

                PlayerFragment.musicListPF.clear()
                if (MyTracksFragment.wasInMyTracks ) {
                    MyTracksFragment.wasInMyTracks = false
                    PlayerFragment.musicListPF.addAll(sortSongs(selectedSortOption, isAscending, 2))
                }
                else{
                    PlayerFragment.musicListPF.addAll(musicListMF)
                }
            }
        }
        val updateList = ArrayList(musicListMF.take(4))
        if (updateList.isEmpty()) {
            binding.viewSwitcher.displayedChild = 1
        } else {
            binding.viewSwitcher.displayedChild = 0
            adapter.updateData(updateList)
            binding.countSongs.text = musicListMF.size.toString()
        }
        Log.d("PF", "MM " + musicListMM.joinToString(" ") { song -> song.title })
        if (PlayerFragment.musicService != null) {
            Log.d("PF", "PF " + PlayerFragment.musicListPF.joinToString(" ") { song -> song.title })
        }
    }

    @SuppressLint("NewApi")
    private fun refreshData() {
        CoroutineScope(Dispatchers.Main).launch {
            delay(10)
            updateSongs(updateLoadSongs = true)
            binding.swipeRefreshLayout.isRefreshing = false
        }
    }
}
