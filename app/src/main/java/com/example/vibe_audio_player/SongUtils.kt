package com.example.vibe_audio_player

import com.example.vibe_audio_player.fragments.MainFragment.Companion.musicListMF
import com.example.vibe_audio_player.fragments.MyTracksFragment.Companion.musicListMTF
import com.example.vibe_audio_player.fragments.MyTracksFragment.Companion.musicListSearch

object SongUtils {
    fun sortSongs(sortOption: String, isAscending: Boolean, mode: Int): ArrayList<Song>{
        val musicListSorted = ArrayList<Song>()

        when(mode){
            0 -> musicListSorted.addAll(musicListSearch)
            1 -> musicListSorted.addAll(musicListMTF)
            2 -> musicListSorted.addAll(musicListMF)
        }

        when (sortOption) {
            "Название песни" -> musicListSorted.sortBy { it.title.lowercase() }
            "Имя артиста" -> musicListSorted.sortBy { it.artist.lowercase() }
            "Название альбома" -> musicListSorted.sortBy { it.album.lowercase() }
            "Время добавления" -> musicListSorted.sortBy { it.dateAdded }
            "Длительность" -> musicListSorted.sortBy { it.duration }
            "Размер" -> musicListSorted.sortBy { it.size }
        }

        if (!isAscending)
            musicListSorted.reverse()

        return musicListSorted
    }
}