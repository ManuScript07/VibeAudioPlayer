package com.example.vibe_audio_player.fragments

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.vibe_audio_player.R
import com.example.vibe_audio_player.Song
import com.example.vibe_audio_player.SortListener
import com.example.vibe_audio_player.activities.MainActivity
import com.example.vibe_audio_player.adapters.SongRVAdapter
import com.example.vibe_audio_player.databinding.FragmentMyTracksBinding
import com.example.vibe_audio_player.fragments.MainFragment.Companion.musicListMF
import com.example.vibe_audio_player.fragments.PlayerFragment.Companion.songPosition


@Suppress("IMPLICIT_CAST_TO_ANY")
class MyTracksFragment : Fragment(), SortListener {
    private lateinit var binding: FragmentMyTracksBinding
    private lateinit var adapter: SongRVAdapter
    var musicListSearch: ArrayList<Song> = ArrayList()
    var musicListSorted: ArrayList<Song> = ArrayList()
    companion object {
        var isShuffle: Boolean = false
        var isSearch: Boolean = false
        var musicListMTF: ArrayList<Song> = ArrayList()
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
        if (musicListMTF.isNotEmpty())
            Toast.makeText(requireContext(), musicListMTF[0].title, Toast.LENGTH_SHORT).show()
        adapter = SongRVAdapter(requireContext(), musicListMTF) { song, position ->
            openPlayerFragment(position)
        }
        if (musicListMTF.isNotEmpty())
            Toast.makeText(requireContext(), musicListMTF[0].title, Toast.LENGTH_SHORT).show()


        binding.rv.apply {
            setHasFixedSize(true)
            setItemViewCacheSize(13)
            layoutManager = LinearLayoutManager(context)
            adapter = this@MyTracksFragment.adapter
        }
//        updateSongs()

        val searchBar = binding.searchBar
        searchBar.clearFocus()
        searchBar.setCompoundDrawablesWithIntrinsicBounds(R.drawable.search, 0, 0, 0) // Скрываем крестик

        searchBar.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun afterTextChanged(s: Editable?) {
                isSearch = if (PlayerFragment.musicService != null) true else false

                binding.btnShuffle.visibility = if (s.isNullOrEmpty()) View.VISIBLE else View.INVISIBLE
                val clearIcon = if (s.isNullOrEmpty()) 0 else R.drawable.baseline_clear_24
                searchBar.setCompoundDrawablesWithIntrinsicBounds(R.drawable.search, 0, clearIcon, 0)
                musicListSearch = ArrayList()
                if(s != null){
                    musicListSearch.addAll(musicListMF.filter { song ->
                        song.title.contains(s, ignoreCase = true) ||
                                song.artist.contains(s, ignoreCase = true)
                    })
                    adapter.updateData(musicListSearch)
                }
            }
        })


        if (PlayerFragment.musicService != null)
            isSearch = true
        else
            isSearch = false

        searchBar.setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_UP) {
                val drawableEnd = searchBar.compoundDrawables[2] ?: return@setOnTouchListener false
                if (event.rawX >= (searchBar.right - drawableEnd.bounds.width())) {
                    searchBar.text.clear()

                    if (!musicListSearch.isEmpty())
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

                // Опционально убираем фокус с поля ввода
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
            val bottomSheet = SortBottomSheetFragment()
            bottomSheet.setSortListener(this)
            bottomSheet.show(parentFragmentManager, "SortBottomSheet")
        }


        binding.btnBack.setOnClickListener{
            findNavController().popBackStack()
        }
    }

    @RequiresApi(Build.VERSION_CODES.R)
    override fun onResume() {
        super.onResume()
        if (adapter.itemCount == 0)
            updateSongs()
    }


    private fun openPlayerFragment(position: Int = 0) {
        val action = PlayerFragmentDirections.actionGlobalPlayerFragment(
            SONGCLASS = (if (musicListMTF[position].id == PlayerFragment.nowPlayingId) "MiniPlayer" else "MyTracks"),
            SONGPOSITION = (if (musicListMTF[position].id == PlayerFragment.nowPlayingId && (isShuffle || isSearch)) songPosition else position),
            NAMEPLAYLIST = (if (musicListMTF[position].id == PlayerFragment.nowPlayingId && isShuffle) "Перемешанное" else "Мои треки")
        )
        if (musicListMTF[position].id != PlayerFragment.nowPlayingId && isShuffle)
            isShuffle = false
        findNavController().navigate(action)
    }


    @SuppressLint("SetTextI18n")
    @RequiresApi(Build.VERSION_CODES.R)
    private fun updateSongs(updateLoadSongs: Boolean = false){
        if (updateLoadSongs) {
            musicListMF = MainActivity.loadTracks(requireContext())
            if (PlayerFragment.musicService != null) {
                PlayerFragment.musicListPF.clear()
                PlayerFragment.musicListPF.addAll(musicListMF) // Чтобы плеер не крашился
            }
        }

        val updateList = ArrayList(musicListMF)
        adapter.updateData(updateList)
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onSortSelected(sortOption: String, isAscending: Boolean) {
        musicListSorted = ArrayList()
        musicListSorted.addAll(musicListMTF)
        when (sortOption) {
            "Название песни" -> musicListSorted.sortBy { it.title.lowercase() }
            "Имя артиста" -> musicListSorted.sortBy { it.artist.lowercase() }
            "Название альбома" -> musicListSorted.sortBy { it.album.lowercase() }
            "Время добавления" -> musicListSorted.sortBy { it.id } // Если id идёт по порядку добавления
            "Длительность" -> musicListSorted.sortBy { it.duration }
            "Размер" -> musicListSorted.sortBy { it.path.length } // Файлы большего размера обычно имеют длинные пути
        }
        if (!isAscending) {
            musicListSorted.reverse()
        }
        adapter.updateData(musicListSorted)
    }
}
