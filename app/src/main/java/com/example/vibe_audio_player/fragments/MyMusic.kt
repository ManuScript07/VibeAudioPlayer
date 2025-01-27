package com.example.vibe_audio_player.fragments

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.vibe_audio_player.R
import com.example.vibe_audio_player.Song
import com.example.vibe_audio_player.activities.MainActivity.Companion.musicListMA
import com.example.vibe_audio_player.activities.PlayerActivity
import com.example.vibe_audio_player.adapters.SongRVAdapter
import com.example.vibe_audio_player.databinding.FragmentMymusicBinding

class MyMusic : Fragment() {

    private lateinit var binding: FragmentMymusicBinding
    private lateinit var adapter: SongRVAdapter
    private lateinit var resultLauncher: ActivityResultLauncher<Intent>

    companion object{
        var musicListMM: ArrayList<Song> = ArrayList()
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
            openPlayerActivity(position)
        }


        binding.recyclerView.apply {
            setHasFixedSize(true)
            setItemViewCacheSize(13)
            layoutManager = LinearLayoutManager(context)
            adapter = this@MyMusic.adapter
        }


        binding.addMusicButton.setOnClickListener {

        }

        binding.playList.setOnClickListener {
            findNavController().navigate(R.id.action_my_music_to_playListFragment)
        }

        binding.goToAllSongs.setOnClickListener{
            findNavController().navigate(R.id.action_my_music_to_myTracksFragment)
        }

        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val bundle = Bundle().apply {
                    putString("artist", result.data?.getStringExtra("artist"))
                }

                findNavController().navigate(R.id.action_my_music_to_aboutArtistFragment, bundle)

            }
        }
    }

    @SuppressLint("NotifyDataSetChanged", "SetTextI18n")
    @RequiresApi(Build.VERSION_CODES.R)
    override fun onResume() {
        super.onResume()
        val updateList = ArrayList(musicListMA.take(4))

        // Проверяем, пустой ли список
        if (updateList.isEmpty()) {
            binding.viewSwitcher.displayedChild = 1 // Показываем сообщение
        } else {
            binding.viewSwitcher.displayedChild = 0 // Показываем RecyclerView
            adapter.updateData(updateList)
        }

        binding.countSongs.text = musicListMA.size.toString()
    }

    // Функция для открытия PlayerActivity
    private fun openPlayerActivity(position: Int) {
        if (isClickAllowed) {
            isClickAllowed = false
            val intent = Intent(context, PlayerActivity::class.java).apply {
                if (musicListMM[position].id == PlayerActivity.nowPlayingId)
                    putExtra("song_class", "MiniPlayer")
                else
                    putExtra("song_class", "MyMusic")
                putExtra("position", position)
                putExtra("namePlayList", "Мои треки")

            }
            resultLauncher.launch(intent)
            binding.root.postDelayed({ isClickAllowed = true }, 500)
        }
//        if (isClickAllowed) {
//            isClickAllowed = false
//            val bundle = Bundle().apply {
//                putString(
//                    "SONG_CLASS",
//                    if (musicListMM[position].id == PlayerFragment.nowPlayingId) "MiniPlayer" else "MyMusic")
//                putInt("SONG_POSITION", position)
//                putString("NAME_PLAYLIST", "Moи треки")
//            }
//
//            findNavController().navigate(R.id.action_my_music_to_playerFragment, bundle)
//
//            binding.root.postDelayed({ isClickAllowed = true }, 500)
//        }
    }
}
