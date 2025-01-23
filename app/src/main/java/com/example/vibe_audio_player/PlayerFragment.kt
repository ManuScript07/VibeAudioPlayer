package com.example.vibe_audio_player

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.GradientDrawable
import android.media.MediaMetadataRetriever
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Button
import android.widget.SeekBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.vibe_audio_player.databinding.FragmentPlayerBinding

/**
 * An example full-screen fragment that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
class PlayerFragment : Fragment(), ServiceConnection, MediaPlayer.OnCompletionListener {

    companion object {
        lateinit var musicListPA: ArrayList<Song>
        var songPosition: Int = 0
        var isPlaying: Boolean = false
        var musicService: MusicService? = null
        var nowPlayingId: String = ""
    }

    private var _binding: FragmentPlayerBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentPlayerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Инициализация
        initializeLayout()

        // Обработчики кликов
        binding.playPause.setOnClickListener {
            if (isPlaying) pauseMusic() else playMusic()
        }

        binding.previous.setOnClickListener {
            previousOrNextSong(false)
        }

        binding.next.setOnClickListener {
            previousOrNextSong(true)
        }

        binding.seekBarPA.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    binding.start.text = formatDuration(progress.toLong())
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                seekBar?.progress?.let {
                    musicService?.mediaPlayer?.seekTo(it)
                }
            }
        })

        binding.back.setOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun initializeLayout() {
        val songClass = arguments?.getString("song_class")
        songPosition = arguments?.getInt("position") ?: 0

        when (songClass) {
            "MyMusic" -> {
                val intent = Intent(requireContext(), MusicService::class.java)
                requireContext().bindService(intent, this, Context.BIND_AUTO_CREATE)
                requireContext().startService(intent)
                musicListPA = ArrayList()
                musicListPA.addAll(MainActivity.musicListMA)
                setLayout()
                binding.namePlaylist.text = "Мои треки"
            }
            "MiniPlayer" -> {
                setLayout()
                binding.start.text = formatDuration(musicService!!.mediaPlayer!!.currentPosition.toLong())
                binding.duration.text = formatDuration(musicService!!.mediaPlayer!!.duration.toLong())
                binding.seekBarPA.progress = musicService!!.mediaPlayer!!.currentPosition
                binding.seekBarPA.max = musicService!!.mediaPlayer!!.duration
                if (!isPlaying)
                    binding.playPause.setIconResource(R.drawable.baseline_play_arrow_24)
            }
        }
        if (musicService != null && !isPlaying) playMusic()
    }

    private fun setLayout() {
        Glide.with(requireContext())
            .load(musicListPA[songPosition].artUri)
            .apply(RequestOptions().placeholder(R.drawable.baseline_music_off_24).centerCrop())
            .into(binding.songImg)

        binding.title.text = musicListPA[songPosition].title
        binding.artist.text = musicListPA[songPosition].artist

        val img = getImgArt(musicListPA[songPosition].path)
        val image = if (img != null) {
            BitmapFactory.decodeByteArray(img, 0, img.size)
        } else {
            BitmapFactory.decodeResource(resources, R.drawable.baseline_music_off_24)
        }

        val bgColor = getMainColor(image)
        val gradient = GradientDrawable(
            GradientDrawable.Orientation.BOTTOM_TOP,
            intArrayOf(0xFFFFFF, bgColor)
        )
        binding.root.background = gradient
        requireActivity().window?.statusBarColor = bgColor
    }

    private fun createMediaPlayer() {
        try {
            if (musicService?.mediaPlayer == null) musicService?.mediaPlayer = MediaPlayer()
            musicService?.mediaPlayer?.reset()
            musicService?.mediaPlayer?.setDataSource(musicListPA[songPosition].path)
            musicService?.mediaPlayer?.prepare()
            binding.start.text = formatDuration(musicService?.mediaPlayer!!.currentPosition.toLong())
            binding.duration.text = formatDuration(musicService?.mediaPlayer!!.duration.toLong())
            binding.seekBarPA.progress = 0
            binding.seekBarPA.max = musicService?.mediaPlayer!!.duration
            musicService?.mediaPlayer!!.setOnCompletionListener(this)
            nowPlayingId = musicListPA[songPosition].id
            playMusic()
        } catch (e: Exception) {
            Toast.makeText(requireContext(), e.toString(), Toast.LENGTH_LONG).show()
        }
    }

    private fun playMusic() {
        binding.playPause.setIconResource(R.drawable.baseline_pause_24)
        isPlaying = true
        musicService?.mediaPlayer?.start()
    }

    private fun pauseMusic() {
        binding.playPause.setIconResource(R.drawable.baseline_play_arrow_24)
        isPlaying = false
        musicService?.mediaPlayer?.pause()
    }

    private fun previousOrNextSong(increment: Boolean) {
        setSongPosition(increment)
        setLayout()
        createMediaPlayer()
    }

    private fun getImgArt(path: String): ByteArray? {
        val retriever = MediaMetadataRetriever()
        retriever.setDataSource(path)
        return retriever.embeddedPicture
    }

    private fun getMainColor(img: Bitmap): Int {
        val newImg = Bitmap.createScaledBitmap(img, 1, 1, true)
        val color = newImg.getPixel(0, 0)
        newImg.recycle()
        return color
    }

    override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
        if (musicService == null) {
            val binder = service as MusicService.MyBinder
            musicService = binder.currentService()
        }
        createMediaPlayer()
        musicService?.seekBarSetup()
    }

    override fun onServiceDisconnected(name: ComponentName?) {
        musicService = null
    }

    override fun onCompletion(mp: MediaPlayer?) {
        previousOrNextSong(true)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
