package com.example.vibe_audio_player

interface SortListener {
    fun onSortSelected(sortOption: String, isAscending: Boolean, mode: Int = 1)
}