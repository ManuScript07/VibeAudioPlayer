package com.example.vibe_audio_player

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.vibe_audio_player.PlayerActivity.Companion.musicListPA
import com.example.vibe_audio_player.PlayerActivity.Companion.songPosition
import com.example.vibe_audio_player.databinding.FragmentMiniPlayerBinding


class MiniPlayer : Fragment() {

    companion object{
        @SuppressLint("StaticFieldLeak")
        lateinit var binding: FragmentMiniPlayerBinding
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_mini_player, container, false)

        binding = FragmentMiniPlayerBinding.bind(view)
        binding.root.visibility = View.INVISIBLE

        binding.playPause.setOnClickListener{
            if (PlayerActivity.isPlaying)
                pauseMusic()
            else
                playMusic()
        }

        binding.next.setOnClickListener{
            setSongPosition(increment = true)
            PlayerActivity.musicService!!.createMediaPlayer()
            Glide.with(requireContext())
                .load(musicListPA[songPosition].artUri)
                .apply(RequestOptions().placeholder(R.drawable.baseline_music_off_24).centerCrop())
                .into(binding.image)

            binding.songName.text = musicListPA[songPosition].title
            binding.artistName.text = musicListPA[songPosition].artist
            playMusic()
        }

        binding.root.setOnClickListener{
            val intent = Intent(requireContext(), PlayerActivity::class.java).apply {
                putExtra("position", songPosition)
                putExtra("song_class", "MiniPlayer")
            }
            startActivity(intent)
        }

        return view
    }

    override fun onResume() {
        super.onResume()
        if (PlayerActivity.musicService != null) {
            binding.root.visibility = View.VISIBLE
            binding.songName.isSelected = true
            Glide.with(requireContext())
                .load(musicListPA[songPosition].artUri)
                .apply(RequestOptions().placeholder(R.drawable.baseline_music_off_24).centerCrop())
                .into(binding.image)
            binding.songName.text = musicListPA[songPosition].title
            binding.artistName.text = musicListPA[songPosition].artist
            if (PlayerActivity.isPlaying)
                binding.playPause.setIconResource(R.drawable.baseline_pause_24)
            else
                binding.playPause.setIconResource(R.drawable.baseline_play_arrow_24)
        }
    }

    private fun playMusic(){
        PlayerActivity.isPlaying = true
        PlayerActivity.musicService!!.mediaPlayer!!.start()
        binding.playPause.setIconResource(R.drawable.baseline_pause_24)
        PlayerActivity.binding.next.setIconResource(R.drawable.baseline_pause_24)

    }

    private fun pauseMusic(){
        PlayerActivity.isPlaying = false
        PlayerActivity.musicService!!.mediaPlayer!!.pause()
        binding.playPause.setIconResource(R.drawable.baseline_play_arrow_24)
        PlayerActivity.binding.next.setIconResource(R.drawable.baseline_play_arrow_24)

    }
}