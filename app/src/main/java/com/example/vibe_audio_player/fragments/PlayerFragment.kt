package com.example.vibe_audio_player.fragments

import android.annotation.SuppressLint
import android.content.ComponentName
import android.content.Context.BIND_AUTO_CREATE
import android.content.Intent
import android.content.ServiceConnection
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.media.MediaMetadataRetriever
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.os.IBinder
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.vibe_audio_player.R
import com.example.vibe_audio_player.Song
import com.example.vibe_audio_player.activities.MainActivity
import com.example.vibe_audio_player.databinding.FragmentPlayerBinding
import com.example.vibe_audio_player.formatDuration
import com.example.vibe_audio_player.services.MusicService
import com.example.vibe_audio_player.setSongPosition
import kotlin.system.exitProcess


/**
 * An example full-screen fragment that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
@Suppress("DEPRECATION")
class PlayerFragment: Fragment(), ServiceConnection, MediaPlayer.OnCompletionListener{

    companion object{
        lateinit var musicListPF: ArrayList<Song>
        var songPosition: Int  = 0
        var isPlaying: Boolean = false
        var musicService: MusicService? = null
        @SuppressLint("StaticFieldLeak")
        lateinit var binding: FragmentPlayerBinding
        var nowPlayingId: String = ""
        var namePlayList: String = ""
    }

//    private val sharedViewModel: SharedViewModel by activityViewModels()
//    private lateinit var songClass: String
//    private lateinit var namePlayLists: String

    private var isUserTouching = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPlayerBinding.inflate(inflater, container, false)
        musicListPF = ArrayList()


        if(requireActivity().intent.data?.scheme.contentEquals("content")){
            songPosition = 0
            val intentService = Intent(requireContext(), MusicService::class.java)
            requireActivity().bindService(intentService, this, BIND_AUTO_CREATE)
            requireActivity().startService(intentService)
            musicListPF = ArrayList()
            musicListPF.add(getMusicDetails(requireActivity().intent.data!!))
            Glide.with(requireContext())
                .load(getImgArt(musicListPF[songPosition].path))
                .apply(RequestOptions().placeholder(R.drawable.baseline_music_off_24).centerCrop())
                .into(binding.songImg)

            binding.title.text = musicListPF[songPosition].title
            binding.artist.text = musicListPF[songPosition].artist
        }else initializeLayout()

        binding.playPause.setOnClickListener{
            if (isPlaying)
                pauseMusic()
            else
                playMusic()
        }

        binding.previous.setOnClickListener{
            previousOrNextSong(false)
        }

        binding.next.setOnClickListener{
            previousOrNextSong(true)
        }
//        var isUserTouching = false
        binding.seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {


            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    isUserTouching = true
                    // Обновляем только текстовое отображение текущего времени
                    binding.start.text = formatDuration(progress.toLong())
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
                isUserTouching = true
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                isUserTouching = false
                // Выполняем seekTo только после завершения перетаскивания
                seekBar?.progress?.let {
                    musicService?.mediaPlayer?.seekTo(it)
                }
            }
        })

        binding.artist.setOnClickListener{
            val navController = findNavController()
            val action = PlayerFragmentDirections.actionPlayerFragmentToAboutArtistFragment(
                ARIST = binding.artist.text.toString()
            )
            navController.navigate(action)

        }

        binding.back.setOnClickListener{
           findNavController().popBackStack()
        }



        return binding.root
    }


    override fun onResume() {
        super.onResume()
        activeStatusBar()
        requireActivity().findViewById<View>(R.id.miniPlayer)?.visibility = View.GONE
    }

    override fun onPause() {
        super.onPause()
        resetStatusBar()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        resetStatusBar()
        requireActivity().findViewById<View>(R.id.miniPlayer)?.visibility = View.VISIBLE
        updateMiniPlayerUI()
    }


    private fun initializeLayout(){
        val args = PlayerFragmentArgs.fromBundle(requireArguments())
        val songClass = args.SONGCLASS
        songPosition = args.SONGPOSITION
        val namePlayLists = args.NAMEPLAYLIST

        when(songClass){
            "MyMusic" -> {
                val intent = Intent(requireContext(), MusicService::class.java)
                requireActivity().bindService(intent, this, BIND_AUTO_CREATE)
                requireActivity().startService(intent)
                musicListPF = ArrayList()
                musicListPF.addAll(MainFragment.musicListMF)
                namePlayList = "Мои треки"
                setLayout()

            }
            "MiniPlayer" -> {
                if (musicListPF.isEmpty())
                    musicListPF.addAll(MainFragment.musicListMF)
                setLayout()

                binding.start.text = formatDuration(musicService!!.mediaPlayer!!.currentPosition.toLong())
                binding.duration.text = formatDuration(musicService!!.mediaPlayer!!.duration.toLong())
                binding.seekBar.progress = musicService!!.mediaPlayer!!.currentPosition
                binding.seekBar.max = musicService!!.mediaPlayer!!.duration

                if (!isPlaying)
                    binding.playPause.setImageResource(R.drawable.baseline_play_arrow_48)
            }
        }

        binding.namePlaylist.text =
            if (namePlayLists == "")
                namePlayList
            else
                namePlayLists

//        if (musicService != null && !isPlaying)
//            playMusic()
    }

    private fun setLayout(){

        Glide.with(binding.songImg)
            .load(musicListPF[songPosition].artUri)
            .apply(RequestOptions().placeholder(R.drawable.baseline_music_off_24).centerCrop())
            .into(binding.songImg)



        binding.title.text = musicListPF[songPosition].title
        binding.artist.text = musicListPF[songPosition].artist


        val img = getImgArt(musicListPF[songPosition].path)

        val image = if (img != null){
            BitmapFactory.decodeByteArray(
                img, 0, img.size
            )
        }else {
            BitmapFactory.decodeResource(
                img, R.drawable.baseline_music_off_24
//                        img, R.drawable.baseline_music_off_24 Если крашится
            )
        }

        if (image != null && image.width > 0 && image.height > 0){
            try {
                val mainColor = getMainColor(image)
                val darkerColor = darkenColor(mainColor, 0.85f)
                val gradient = GradientDrawable(
                    GradientDrawable.Orientation.TOP_BOTTOM,
                    intArrayOf(darkerColor, mainColor)
                )
                binding.root.background = gradient
                activeStatusBar()

            }catch (e: Exception){
                binding.root.background = GradientDrawable(
                    GradientDrawable.Orientation.TOP_BOTTOM,
                    intArrayOf(Color.BLACK, Color.DKGRAY)
                )
                activeStatusBar()

            }
        } else {
            binding.root.background = GradientDrawable(
                GradientDrawable.Orientation.TOP_BOTTOM,
                intArrayOf(Color.BLACK, Color.DKGRAY)
            )
            activeStatusBar()
        }
    }


    private fun createMediaPlayer(){
        try {
            if (musicService!!.mediaPlayer == null)
                musicService!!.mediaPlayer = MediaPlayer()
            musicService!!.mediaPlayer!!.reset()
            musicService!!.mediaPlayer!!.setDataSource(musicListPF[songPosition].path)
            musicService!!.mediaPlayer!!.prepare()
            binding.start.text = formatDuration(musicService!!.mediaPlayer!!.currentPosition.toLong())
            binding.duration.text = formatDuration(musicService!!.mediaPlayer!!.duration.toLong())
            binding.seekBar.progress = 0
            binding.seekBar.max = musicService!!.mediaPlayer!!.duration
            musicService!!.mediaPlayer!!.setOnCompletionListener(this)
            nowPlayingId = musicListPF[songPosition].id
            playMusic()

        }catch (e: Exception){ Toast.makeText(context, e.toString(), Toast.LENGTH_LONG).show()}
    }

    private fun getMusicDetails(contentUri: Uri): Song {
        var cursor: Cursor? = null
        try {
            val projection = arrayOf(MediaStore.Audio.Media.DATA, MediaStore.Audio.Media.DURATION)
            cursor = context?.contentResolver?.query(contentUri, projection, null, null, null)
            val dataColumn = cursor?.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA)
            val durationColumn = cursor?.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION)
            cursor!!.moveToFirst()
            val path = dataColumn?.let { cursor.getString(it) }
            val duration = durationColumn?.let { cursor.getLong(it) }!!
            return Song(id = "Unknown", title = path.toString(), album = "Unknown", artist = "Unknown", duration = duration,
                artUri = "Unknown", path = path.toString())
        }finally {
            cursor?.close()
        }
    }


    private fun getBrightness(color: Int): Double {
        val red = Color.red(color)
        val green = Color.green(color)
        val blue = Color.blue(color)
        return (0.299 * red + 0.587 * green + 0.114 * blue)
    }



    private fun darkenColor(color: Int, factor: Float): Int {
        val red = (Color.red(color) * factor).toInt()
        val green = (Color.green(color) * factor).toInt()
        val blue = (Color.blue(color) * factor).toInt()
        return Color.rgb(red, green, blue)
    }

    private fun getMainColor(img: Bitmap): Int {
        return try {
            val newImg = Bitmap.createScaledBitmap(img, 1, 1, true)
            val color = newImg.getPixel(0, 0)
            newImg.recycle()

            val brightness = getBrightness(color)
            if (brightness > 128)
                darkenColor(color, 0.5f)
            else
                color
        } catch (e: Exception){
            0xFFFFFF
        }
    }




    private fun getImgArt(path: String): ByteArray?{
        val retriever = MediaMetadataRetriever()
        retriever.setDataSource(path)
        return retriever.embeddedPicture
    }
    private fun playMusic(){
        binding.playPause.setImageResource(R.drawable.baseline_pause_48)
        isPlaying = true
        musicService!!.mediaPlayer!!.start()
    }

    private fun pauseMusic(){
        binding.playPause.setImageResource(R.drawable.baseline_play_arrow_48)
        isPlaying = false
        musicService!!.mediaPlayer!!.pause()
    }

    private fun previousOrNextSong(increment: Boolean){
        if(increment) {
            setSongPosition(increment = true)
            setLayout()
            createMediaPlayer()
        }
        else {
            setSongPosition(increment = false)
            setLayout()
            createMediaPlayer()
        }
    }

    override fun onCompletion(mp: MediaPlayer?) {
        setSongPosition(increment = true)
        resetStatusBar()
        createMediaPlayer()
        setLayout()

        MainFragment.binding.songName.isSelected = true

        Glide.with(MainFragment.binding.image)
            .load(musicListPF[songPosition].artUri)
            .apply(
                RequestOptions().placeholder(R.drawable.baseline_music_off_24).centerCrop()
            )
            .into(MainFragment.binding.image)

        MainFragment.binding.songName.text = musicListPF[songPosition].title
        MainFragment.binding.artistName.text = musicListPF[songPosition].artist

    }

    override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
        if(musicService == null){
            val binder = service as MusicService.MyBinder
            musicService = binder.currentService()}
        createMediaPlayer()
        musicService!!.seekBarSetup()
    }

    override fun onServiceDisconnected(p0: ComponentName?) {
        musicService = null
    }

    override fun onDestroy() {
        super.onDestroy()
        if(musicListPF[songPosition].id == "Unknown" && !isPlaying) exitApplication()
    }

    private fun exitApplication() {
        if (musicService != null) {
            musicService!!.mediaPlayer!!.release()
            musicService = null
        }
        exitProcess(1)
    }

    private fun updateMiniPlayerUI() {
        if (musicService != null) {

            MainActivity.binding.root.visibility = View.VISIBLE
            MainFragment.binding.songName.isSelected = true
            Glide.with(MainFragment.binding.image)
                .load(musicListPF[songPosition].artUri)
                .apply(RequestOptions().placeholder(R.drawable.baseline_music_off_24).centerCrop())
                .into(MainFragment.binding.image)
            MainFragment.binding.songName.text = musicListPF[songPosition].title
            MainFragment.binding.artistName.text = musicListPF[songPosition].artist
            if (isPlaying)
                MainFragment.binding.playPause.setImageResource(R.drawable.baseline_pause_32)

            else
                MainFragment.binding.playPause.setImageResource(R.drawable.baseline_play_arrow_32)
        }
    }


    private fun resetStatusBar() {
        activity?.window?.apply {
            statusBarColor = ContextCompat.getColor(requireContext(), R.color.purple_500)
            decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_VISIBLE
        }
    }

    private fun activeStatusBar(){
        activity?.window?.apply {
            statusBarColor = Color.TRANSPARENT
            decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
        }
    }
}


