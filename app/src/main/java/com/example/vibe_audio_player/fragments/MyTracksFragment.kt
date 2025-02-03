package com.example.vibe_audio_player.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.vibe_audio_player.R
import com.example.vibe_audio_player.adapters.SongRVAdapter
import com.example.vibe_audio_player.databinding.FragmentMyTracksBinding
import com.example.vibe_audio_player.fragments.MainFragment.Companion.musicListMF
import com.example.vibe_audio_player.fragments.PlayerFragment.Companion.songPosition


@Suppress("IMPLICIT_CAST_TO_ANY")
class MyTracksFragment : Fragment() {
    private lateinit var binding: FragmentMyTracksBinding
    private lateinit var adapter: SongRVAdapter
    companion object {
        var isShuffle: Boolean = false
    }
    val _position: Int = 0
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMyTracksBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = SongRVAdapter(requireContext(), musicListMF) { song, position ->
            openPlayerFragment(position)

        }

        binding.rv.apply {
            setHasFixedSize(true)
            setItemViewCacheSize(13)
            layoutManager = LinearLayoutManager(context)
            adapter = this@MyTracksFragment.adapter
        }

        val searchBar = binding.searchBar
        searchBar.setCompoundDrawablesWithIntrinsicBounds(R.drawable.search, 0, 0, 0) // Скрываем крестик

        searchBar.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                binding.btnShuffle.visibility = if (s.isNullOrEmpty()) View.VISIBLE else View.INVISIBLE
                val clearIcon = if (s.isNullOrEmpty()) 0 else R.drawable.baseline_clear_24
                searchBar.setCompoundDrawablesWithIntrinsicBounds(R.drawable.search, 0, clearIcon, 0)
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        searchBar.setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_UP) {
                val drawableEnd = searchBar.compoundDrawables[2] ?: return@setOnTouchListener false
                if (event.rawX >= (searchBar.right - drawableEnd.bounds.width())) {
                    searchBar.text.clear()
                    return@setOnTouchListener true
                }
            }
            false
        }

        binding.btnShuffle.setOnClickListener{
            isShuffle = true
            val action = PlayerFragmentDirections.actionGlobalPlayerFragment(
                SONGCLASS = "Shuffle",
                SONGPOSITION = 0,
                NAMEPLAYLIST = "Перемешанное"
            )
            findNavController().navigate(action)
        }

        binding.btnBack.setOnClickListener{
            findNavController().popBackStack()
        }

    }



    private fun openPlayerFragment(position: Int = 0) {
        val action = PlayerFragmentDirections.actionGlobalPlayerFragment(
            SONGCLASS = (if (musicListMF[position].id == PlayerFragment.nowPlayingId) "MiniPlayer" else "MyMusic"),
            SONGPOSITION = (if (musicListMF[position].id == PlayerFragment.nowPlayingId && isShuffle) songPosition else position),
            NAMEPLAYLIST = (if (musicListMF[position].id == PlayerFragment.nowPlayingId && isShuffle) "Перемешанное" else "Мои треки")
        )
        if (musicListMF[position].id != PlayerFragment.nowPlayingId && isShuffle)
            isShuffle = false
        findNavController().navigate(action)
    }
}
