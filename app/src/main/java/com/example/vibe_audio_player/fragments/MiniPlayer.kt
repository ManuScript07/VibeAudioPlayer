package com.example.vibe_audio_player.fragments

import android.annotation.SuppressLint
import android.media.MediaPlayer
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.vibe_audio_player.R
import com.example.vibe_audio_player.databinding.FragmentMiniPlayerBinding
import com.example.vibe_audio_player.fragments.PlayerFragment.Companion.musicListPF
import com.example.vibe_audio_player.fragments.PlayerFragment.Companion.songPosition
import com.example.vibe_audio_player.setSongPosition


class MiniPlayer : Fragment(), MediaPlayer.OnCompletionListener{

    companion object{
        @SuppressLint("StaticFieldLeak")
        lateinit var binding: FragmentMiniPlayerBinding
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMiniPlayerBinding.inflate(layoutInflater, container, false)

        //binding.root.visibility = View.INVISIBLE
        //updateMiniPlayerUI()
        binding.playPause.setOnClickListener{
            if (PlayerFragment.isPlaying)
                pauseMusic()
            else
                playMusic()
        }

        binding.next.setOnClickListener{
            setSongPosition(increment = true)
            PlayerFragment.musicService!!.createMediaPlayer()
            Glide.with(requireContext())
                .load(musicListPF[songPosition].artUri)
                .apply(RequestOptions().placeholder(R.drawable.baseline_music_off_24).centerCrop())
                .into(binding.image)

            binding.songName.text = musicListPF[songPosition].title
            binding.artistName.text = musicListPF[songPosition].artist
            playMusic()
        }

        binding.root.setOnClickListener{

            val bundle = Bundle().apply {
                putString("SONG_CLASS", "MiniPlayer")
                putInt("SONG_POSITION", songPosition)
                putString("NAME_PLAYLIST", "")
            }
            findNavController().navigate(R.id.action_my_music_to_playerFragment, bundle)
        }

        return binding.root
    }


    override fun onResume() {
        super.onResume()
        if (PlayerFragment.musicService != null) {
            binding.root.visibility = View.VISIBLE
           // Toast.makeText(context, "asfsdf", Toast.LENGTH_SHORT).show()
            //updateMiniPlayerUI()
        }
    }



    private fun updateMiniPlayerUI() {
        if (PlayerFragment.musicService != null) {
            binding.root.visibility = View.VISIBLE
            binding.songName.isSelected = true
            Glide.with(requireContext())
                .load(musicListPF[songPosition].artUri)
                .apply(RequestOptions().placeholder(R.drawable.baseline_music_off_24).centerCrop())
                .into(binding.image)
            binding.songName.text = musicListPF[songPosition].title
            binding.artistName.text = musicListPF[songPosition].artist
            if (PlayerFragment.isPlaying)
                binding.playPause.setImageResource(R.drawable.baseline_pause_24)

            else
                binding.playPause.setImageResource(R.drawable.baseline_play_arrow_24)
        }
    }

    private fun playMusic(){
        PlayerFragment.isPlaying = true
        PlayerFragment.musicService!!.mediaPlayer!!.start()
        binding.playPause.setImageResource(R.drawable.baseline_pause_24)
        PlayerFragment.binding.next.setImageResource(R.drawable.baseline_pause_24)

    }

    private fun pauseMusic(){
        PlayerFragment.isPlaying = false
        PlayerFragment.musicService!!.mediaPlayer!!.pause()
        binding.playPause.setImageResource(R.drawable.baseline_play_arrow_24)
        PlayerFragment.binding.next.setImageResource(R.drawable.baseline_play_arrow_24)

    }
    override fun onCompletion(mp: MediaPlayer?) {
        setSongPosition(increment = true)


        binding.songName.isSelected = true
        Glide.with(requireContext())
            .load(musicListPF[songPosition].artUri)
            .apply(RequestOptions().placeholder(R.drawable.baseline_music_off_24).centerCrop())
            .into(binding.image)
        binding.songName.text = musicListPF[songPosition].title
    }
}