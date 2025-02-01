package com.example.vibe_audio_player.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.vibe_audio_player.R
import com.example.vibe_audio_player.databinding.FragmentAboutArtistBinding


class AboutArtistFragment : Fragment() {

    private lateinit var binding: FragmentAboutArtistBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentAboutArtistBinding.inflate(layoutInflater, container, false)

        val navController = findNavController()
        val isPlayerInBackStack = try {
            navController.getBackStackEntry(R.id.playerFragment)
            true
        } catch (e: IllegalArgumentException) {
            false
        }
        if (isPlayerInBackStack)
            MainFragment.binding.bottomNavigationView.visibility = View.GONE
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val args = AboutArtistFragmentArgs.fromBundle(requireArguments())
        val artist = args.ARIST
        binding.artist.text = artist

        // Чтобы не возвращаться в плеер, а перходить к пердпредыдущему фрагменту
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner){
            val navController = findNavController()
            val isPlayerInBackStack = try {
                navController.getBackStackEntry(R.id.playerFragment)
                true
            } catch (e: IllegalArgumentException) {
                false
            }

            if (isPlayerInBackStack)
                navController.popBackStack(R.id.playerFragment, true)
            else
                navController.popBackStack()
        }

    }
}