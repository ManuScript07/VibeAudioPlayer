package com.example.vibe_audio_player

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.vibe_audio_player.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {

    private lateinit var binding : ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        replaceFragment(MyMusic())

        binding.bottomNavigationView.setOnItemSelectedListener {
            when(it.itemId){
                R.id.my_music -> replaceFragment(MyMusic())
                R.id.for_you -> replaceFragment(ForYou())
                R.id.stream -> replaceFragment(Stream())
                R.id.search -> replaceFragment(Search())
                R.id.profile -> replaceFragment(Profile())

                else -> {

                }
            }
            true
        }

    }

    private fun replaceFragment(fragment: Fragment) {
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.frame_layout, fragment)
        fragmentTransaction.commit()

    }
}
