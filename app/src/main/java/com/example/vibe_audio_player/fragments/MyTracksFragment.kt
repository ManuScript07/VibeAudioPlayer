package com.example.vibe_audio_player.fragments

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.vibe_audio_player.R
import com.example.vibe_audio_player.Song
import com.example.vibe_audio_player.SongUtils.sortSongs
import com.example.vibe_audio_player.SortListener
import com.example.vibe_audio_player.activities.MainActivity
import com.example.vibe_audio_player.adapters.SongRVAdapter
import com.example.vibe_audio_player.databinding.FragmentMyTracksBinding
import com.example.vibe_audio_player.fragments.MainFragment.Companion.musicListMF
import com.example.vibe_audio_player.fragments.PlayerFragment.Companion.songPosition
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class MyTracksFragment : Fragment(), SortListener {
    private lateinit var binding: FragmentMyTracksBinding
    private lateinit var adapter: SongRVAdapter
    private lateinit var sharedPrefs: SharedPreferences

    private var selectedSortOption: String = "Название песни"
    private var isAscending: Boolean = true
    private var isClickAllowed = true
    private var isMusicListPFInitialized = false
    private var musicListSorted: ArrayList<Song> = ArrayList()



    companion object {
        var isShuffle: Boolean = false
        var isSearch: Boolean = false
        var musicListMTF: ArrayList<Song> = ArrayList()
        var musicListSearch: ArrayList<Song> = ArrayList()
        var wasInMyTracks: Boolean = false
        var isSearchingMTF: Boolean = false
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMyTracksBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.R)
    @SuppressLint("ClickableViewAccessibility")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sharedPrefs = requireContext().getSharedPreferences("SortPreferences", Context.MODE_PRIVATE)
        loadInitializationFlag()
        loadUserChoice()
        wasInMyTracks = true
        adapter = SongRVAdapter(requireContext(), musicListMTF) { song, position ->
            openPlayerFragment(position)
        }

        binding.rv.apply {
            setHasFixedSize(true)
            setItemViewCacheSize(13)
            layoutManager = LinearLayoutManager(context)
            adapter = this@MyTracksFragment.adapter
        }

        updateSongs()

        val searchBar = binding.searchBar
        searchBar.clearFocus()
        searchBar.setCompoundDrawablesWithIntrinsicBounds(R.drawable.search, 0, 0, 0) // Скрываем крестик

        searchBar.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun afterTextChanged(s: Editable?) {
                isSearch = PlayerFragment.musicService != null
                isSearchingMTF = !s.isNullOrEmpty()
                binding.btnShuffle.visibility = if (s.isNullOrEmpty()) View.VISIBLE else View.INVISIBLE
                val clearIcon = if (s.isNullOrEmpty()) 0 else R.drawable.baseline_clear_24
                searchBar.setCompoundDrawablesWithIntrinsicBounds(R.drawable.search, 0, clearIcon, 0)
                musicListSearch = ArrayList()

                if(s != null) {
                    musicListSearch.addAll(musicListMF.filter { song ->
                        song.title.contains(s, ignoreCase = true) ||
                                song.artist.contains(s, ignoreCase = true)
                    })
                    onSortSelected(selectedSortOption, isAscending, 0)
                }
            }
        })

        isSearch = PlayerFragment.musicService != null

        searchBar.setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_UP) {
                val drawableEnd = searchBar.compoundDrawables[2] ?: return@setOnTouchListener false
                if (event.rawX >= (searchBar.right - drawableEnd.bounds.width())) {
                    searchBar.text.clear()
                    isSearchingMTF = false
                    if (musicListSearch.isNotEmpty())
                        isSearch = true
                    return@setOnTouchListener true
                }
            }
            false
        }

        searchBar.setOnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_ACTION_SEARCH) {
                // Скрываем клавиатуру
                val imm = v.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(v.windowToken, 0)
                searchBar.clearFocus()
                return@setOnEditorActionListener true
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

        binding.btnSort.setOnClickListener{
            searchBar.clearFocus()
            val bottomSheet = SortBottomSheetFragment()
            bottomSheet.setSortListener(this)
            bottomSheet.show(parentFragmentManager, "SortBottomSheet")
        }

        binding.swipeRefreshLayout.setColorSchemeResources(
            R.color.purple_500
        )

        binding.swipeRefreshLayout.setOnRefreshListener {
            refreshData()
        }

        binding.btnBack.setOnClickListener{
            findNavController().popBackStack()
        }
    }

    @SuppressLint("NewApi")
    private fun refreshData() {
        CoroutineScope(Dispatchers.Main).launch {
            delay(10)
            if (!isSearchingMTF)
                updateSongs(updateLoadSongs = true)
            binding.swipeRefreshLayout.isRefreshing = false
        }
    }

    @RequiresApi(Build.VERSION_CODES.R)
    override fun onResume() {
        super.onResume()
        if (adapter.itemCount == 0)
            updateSongs()
    }


    private fun openPlayerFragment(position: Int = 0) {
        if (isClickAllowed) {
            isClickAllowed = false
            val action = PlayerFragmentDirections.actionGlobalPlayerFragment(
                SONGCLASS = (if (musicListMTF[position].id == PlayerFragment.nowPlayingId) "MiniPlayer" else "MyTracks"),
                SONGPOSITION = (if (musicListMTF[position].id == PlayerFragment.nowPlayingId && (isShuffle || isSearch)) songPosition else position),
                NAMEPLAYLIST = (if (musicListMTF[position].id == PlayerFragment.nowPlayingId && isShuffle) "Перемешанное" else "Мои треки")
            )
            if (musicListMTF[position].id != PlayerFragment.nowPlayingId && isShuffle)
                isShuffle = false
            findNavController().navigate(action)

            binding.root.postDelayed({ isClickAllowed = true }, 500)
        }
    }

    @SuppressLint("SetTextI18n")
    @RequiresApi(Build.VERSION_CODES.R)
    private fun updateSongs(updateLoadSongs: Boolean = false){
        if (updateLoadSongs)
            musicListMF = MainActivity.loadTracks(requireContext())

        loadInitializationFlag()

        if (!isMusicListPFInitialized && PlayerFragment.musicService != null) {
            PlayerFragment.musicListPF.clear()
            PlayerFragment.musicListPF.addAll(sortSongs(selectedSortOption, isAscending, 2)) // Чтобы плеер не крашился
            isMusicListPFInitialized = true
            saveInitializationFlag()
        }

        onSortSelected(selectedSortOption, isAscending, 2)
        Log.d("PF", "MTF " + musicListMTF.joinToString(" ") { song -> song.title })
        if (PlayerFragment.musicService != null) {
            Log.d("PF", "PF " + PlayerFragment.musicListPF.joinToString(" ") { song -> song.title })
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onSortSelected(sortOption: String, isAscending: Boolean, mode: Int) {
        loadUserChoice()
        adapter.updateData(sortSongs(sortOption, isAscending, mode))
    }

//    private fun sortSongs(sortOption: String, isAscending: Boolean, mode: Int): ArrayList<Song>{
//        musicListSorted = ArrayList()
//
//        when(mode){
//            0 -> musicListSorted.addAll(musicListSearch)
//            1 -> musicListSorted.addAll(musicListMTF)
//            2 -> musicListSorted.addAll(musicListMF)
//        }
//
//        when (sortOption) {
//            "Название песни" -> musicListSorted.sortBy { it.title.lowercase() }
//            "Имя артиста" -> musicListSorted.sortBy { it.artist.lowercase() }
//            "Название альбома" -> musicListSorted.sortBy { it.album.lowercase() }
//            "Время добавления" -> musicListSorted.sortBy { it.dateAdded }
//            "Длительность" -> musicListSorted.sortBy { it.duration }
//            "Размер" -> musicListSorted.sortBy { it.size }
//        }
//
//        if (!isAscending)
//            musicListSorted.reverse()
//
//
//        return musicListSorted
//    }

    private fun loadUserChoice() {
        selectedSortOption = sharedPrefs.getString("SORT_OPTION", "Название песни") ?: "Название песни"
        isAscending = sharedPrefs.getBoolean("SORT_ORDER", true)
    }

    private fun loadInitializationFlag() {
        isMusicListPFInitialized = sharedPrefs.getBoolean("IS_PF_INITIALIZED", false)
    }

    private fun saveInitializationFlag() {
        sharedPrefs.edit().putBoolean("IS_PF_INITIALIZED", true).apply()
    }

}
