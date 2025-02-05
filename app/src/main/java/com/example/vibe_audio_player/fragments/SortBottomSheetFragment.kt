package com.example.vibe_audio_player.fragments

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.vibe_audio_player.R
import com.example.vibe_audio_player.SortListener
import com.example.vibe_audio_player.databinding.FragmentSortBottomSheetBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class SortBottomSheetFragment : BottomSheetDialogFragment() {

    private lateinit var sharedPrefs: SharedPreferences
    private lateinit var binding: FragmentSortBottomSheetBinding

    private var sortListener: SortListener? = null
    private var selectedSortOption: String = "Название песни"
    private var isAscending: Boolean = true

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSortBottomSheetBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    fun setSortListener(listener: SortListener) {
        this.sortListener = listener
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sharedPrefs = requireContext().getSharedPreferences("SortPreferences", Context.MODE_PRIVATE)

        loadUserChoice()

        binding.radioGroupSort.setOnCheckedChangeListener { _, checkedId ->
            selectedSortOption = when (checkedId) {
                R.id.radioSongName -> "Название песни"
                R.id.radioArtistName -> "Имя артиста"
                R.id.radioAlbumName -> "Название альбома"
                R.id.radioAddedTime -> "Время добавления"
                R.id.radioDuration -> "Длительность"
                R.id.radioSize -> "Размер"
                else -> selectedSortOption
            }
        }


        binding.toggleOrder.setOnCheckedChangeListener { _, isChecked ->
            isAscending = isChecked
        }

        binding.buttonCancel.setOnClickListener {
            dismiss()
        }

        binding.buttonOk.setOnClickListener {
            saveUserChoice(selectedSortOption, isAscending)
            sortListener?.onSortSelected(selectedSortOption, isAscending)
            dismiss()
        }


        dialog?.setOnShowListener { dialog ->
            val bottomSheetDialog = dialog as com.google.android.material.bottomsheet.BottomSheetDialog
            val bottomSheet = bottomSheetDialog.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
            bottomSheet?.setBackgroundResource(R.drawable.bottom_sheet_background)
        }

    }
    private fun saveUserChoice(sortOption: String, isAscending: Boolean) {
        sharedPrefs.edit()
            .putString("SORT_OPTION", sortOption)
            .putBoolean("SORT_ORDER", isAscending)
            .apply()
    }

    private fun loadUserChoice() {
        selectedSortOption = sharedPrefs.getString("SORT_OPTION", "Название песни") ?: "Название песни"
        isAscending = sharedPrefs.getBoolean("SORT_ORDER", true)

        val radioGroup = binding.radioGroupSort

        when (selectedSortOption) {
            "Название песни" -> radioGroup.check(R.id.radioSongName)
            "Имя артиста" -> radioGroup.check(R.id.radioArtistName)
            "Название альбома" -> radioGroup.check(R.id.radioAlbumName)
            "Время добавления" -> radioGroup.check(R.id.radioAddedTime)
            "Длительность" -> radioGroup.check(R.id.radioDuration)
            "Размер" -> radioGroup.check(R.id.radioSize)
        }

        binding.toggleOrder.isChecked = isAscending
    }
}
