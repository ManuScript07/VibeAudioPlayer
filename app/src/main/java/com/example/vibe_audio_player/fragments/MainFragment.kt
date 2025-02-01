package com.example.vibe_audio_player.fragments

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.vibe_audio_player.R
import com.example.vibe_audio_player.Song
import com.example.vibe_audio_player.activities.MainActivity
import com.example.vibe_audio_player.databinding.FragmentMainBinding
import com.example.vibe_audio_player.fragments.PlayerFragment.Companion.musicListPF
import com.example.vibe_audio_player.fragments.PlayerFragment.Companion.songPosition
import com.example.vibe_audio_player.setSongPosition
import com.google.android.material.bottomnavigation.BottomNavigationView


class MainFragment : Fragment() {


    companion object{
        var musicListMF: ArrayList<Song> = ArrayList()
        @SuppressLint("StaticFieldLeak")
        lateinit var binding: FragmentMainBinding
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMainBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.R)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (MainActivity.permission)
            musicListMF = MainActivity.loadTracks(requireContext())

        binding.playPause.setOnClickListener {
            if (PlayerFragment.isPlaying)
                pauseMusic()
            else
                playMusic()
        }


        binding.next.setOnClickListener {
            setSongPosition(increment = true)
            PlayerFragment.musicService!!.createMediaPlayer()
            Glide.with(this)
                .load(musicListPF[songPosition].artUri)
                .apply(RequestOptions().placeholder(R.drawable.baseline_music_off_24).centerCrop())
                .into(binding.image)

            binding.songName.text = musicListPF[songPosition].title
            binding.artistName.text = musicListPF[songPosition].artist
            playMusic()
        }

        val navController = (childFragmentManager.findFragmentById(R.id.mainContainerView) as NavHostFragment)
            .navController

        binding.miniPlayer.setOnClickListener {
            val action = PlayerFragmentDirections.actionGlobalPlayerFragment(
                SONGCLASS = "MiniPlayer",
                SONGPOSITION = songPosition,
                NAMEPLAYLIST = "Мои треки"
            )
            navController.navigate(action)
        }

        val bottomNavigationView = view.findViewById<BottomNavigationView>(R.id.bottomNavigationView)
        bottomNavigationView.setupWithNavController(navController)
        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.playerFragment -> {
                    binding.bottomNavigationView.visibility = View.GONE
                    binding.bottomNavigationView.animate()
                        .translationY(binding.bottomNavigationView.height.toFloat())
                        .setDuration(300)
                        .start()
                }
                else -> {
                    binding.bottomNavigationView.visibility = View.VISIBLE
                    binding.bottomNavigationView.animate()
                        .translationY(0f)
                        .setDuration(300)
                        .start()
                }
            }
        }


    }

    override fun onResume() {
        super.onResume()
        if (PlayerFragment.musicService != null) {
            binding.miniPlayer.visibility = View.VISIBLE
            binding.songName.isSelected = true
            Glide.with(requireActivity())
                .load(musicListPF[songPosition].artUri)
                .apply(RequestOptions().placeholder(R.drawable.baseline_music_off_24).centerCrop())
                .into(binding.image)
            binding.songName.text = musicListPF[songPosition].title
            binding.artistName.text = musicListPF[songPosition].artist
            if (PlayerFragment.isPlaying)
                binding.playPause.setImageResource(R.drawable.baseline_pause_32)

            else
                binding.playPause.setImageResource(R.drawable.baseline_play_arrow_32)
        }
    }

    private fun playMusic(){
        PlayerFragment.isPlaying = true
        PlayerFragment.musicService!!.mediaPlayer!!.start()
        binding.playPause.setImageResource(R.drawable.baseline_pause_32)
        PlayerFragment.binding.next.setImageResource(R.drawable.baseline_pause_32)

    }

    private fun pauseMusic(){
        PlayerFragment.isPlaying = false
        PlayerFragment.musicService!!.mediaPlayer!!.pause()
        binding.playPause.setImageResource(R.drawable.baseline_play_arrow_32)
        PlayerFragment.binding.next.setImageResource(R.drawable.baseline_play_arrow_32)

    }

}