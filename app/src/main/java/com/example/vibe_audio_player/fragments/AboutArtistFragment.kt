package com.example.vibe_audio_player.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.vibe_audio_player.databinding.FragmentAboutArtistBinding


class AboutArtistFragment : Fragment() {

    private lateinit var binding: FragmentAboutArtistBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentAboutArtistBinding.inflate(layoutInflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val args = AboutArtistFragmentArgs.fromBundle(requireArguments())
        val artist = args.ARIST
        binding.artist.text = artist
    }
}