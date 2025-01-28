package com.example.vibe_audio_player.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.vibe_audio_player.adapters.SongRVAdapter
import com.example.vibe_audio_player.databinding.FragmentMyTracksBinding
import com.example.vibe_audio_player.fragments.MyMusic.Companion.musicListMM


class MyTracksFragment : Fragment() {
    companion object {
        fun newInstance(data: String?): MyMusic {
            val fragment = MyMusic()
            val args = Bundle()
            args.putString("artist", data)
            fragment.arguments = args
            return fragment
        }
    }
    private lateinit var binding: FragmentMyTracksBinding
    private lateinit var adapter: SongRVAdapter


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMyTracksBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val artist = arguments?.getString("artist")

        binding.text.text = artist
        adapter = SongRVAdapter(requireContext(), musicListMM) { song, position ->
            openPlayerActivity(position)
        }

        binding.rv.apply {
            setHasFixedSize(true)
            setItemViewCacheSize(13)
            layoutManager = LinearLayoutManager(context)
            adapter = this@MyTracksFragment.adapter
        }
    }


    private fun openPlayerActivity(position: Int) {
        val action = PlayerFragmentDirections.actionGlobalPlayerFragment(
            SONGCLASS = (if (musicListMM[position].id == PlayerFragment.nowPlayingId) "MiniPlayer" else "MyMusic"),
            SONGPOSITION = position,
            NAMEPLAYLIST = "Мои треки"
        )
        findNavController().navigate(action)
    }
}
