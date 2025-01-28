package com.example.vibe_audio_player.fragments

import android.annotation.SuppressLint
import android.content.ComponentName
import android.content.Context
import android.content.Context.BIND_AUTO_CREATE
import android.content.Intent
import android.content.ServiceConnection
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
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
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.vibe_audio_player.R
import com.example.vibe_audio_player.SharedViewModel
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
        lateinit var context: Context
    }

    private val sharedViewModel: SharedViewModel by activityViewModels()
    private lateinit var songClass: String
    private lateinit var namePlayLists: String
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPlayerBinding.inflate(inflater, container, false)
        musicListPF = ArrayList()

        sharedViewModel.songClass.observe(viewLifecycleOwner) { sonп ->
            songClass = sonп
        }

        sharedViewModel.songPosition.observe(viewLifecycleOwner) { position ->
            songPosition = position
        }

        sharedViewModel.songPosition.observe(viewLifecycleOwner) { PlayList ->
            namePlayList = PlayList.toString()
        }

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

        binding.seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            private var isUserTouching = false

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
            //Toast.makeText(requireContext(), binding.artist.text.toString(), Toast.LENGTH_SHORT).show()
            val action = PlayerFragmentDirections.actionPlayerFragmentToAboutArtistFragment(
                ARIST = binding.artist.text.toString()
            )
            findNavController().navigate(action)
        }

        binding.back.setOnClickListener{
           findNavController().popBackStack()
        }



        return binding.root
    }

//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//
//
//    }

    override fun onResume() {
        super.onResume()
        //Toast.makeText(context, "orpa", Toast.LENGTH_SHORT).show()
        requireActivity().findViewById<View>(R.id.miniPlayer)?.visibility = View.GONE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        //Toast.makeText(context, "odpa", Toast.LENGTH_SHORT).show()
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
                musicListPF.addAll(MainActivity.musicListMA)
                namePlayList = "Мои треки"
                setLayout()

            }
            "MiniPlayer" -> {
                if (musicListPF.isEmpty())
                    musicListPF.addAll(MainActivity.musicListMA)
                setLayout()

                binding.start.text = formatDuration(musicService!!.mediaPlayer!!.currentPosition.toLong())
                binding.duration.text = formatDuration(musicService!!.mediaPlayer!!.duration.toLong())
                binding.seekBar.progress = musicService!!.mediaPlayer!!.currentPosition
                binding.seekBar.max = musicService!!.mediaPlayer!!.duration

                if (!isPlaying)
                    binding.playPause.setImageResource(R.drawable.baseline_play_arrow_24)
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
                resources, R.drawable.baseline_music_off_24
            )
        }

        val bgColor = getMainColor(image)
        val gradient = GradientDrawable(
            GradientDrawable.Orientation.BOTTOM_TOP,
            intArrayOf(0xFFFFFF, bgColor)
        )
        binding.root.background = gradient
        activity?.window?.statusBarColor = bgColor
    }


    private fun createMediaPlayer(){
        try {
            if (musicService!!.mediaPlayer == null) musicService!!.mediaPlayer = MediaPlayer()
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

    private fun getMainColor(img: Bitmap): Int {
        val newImg = Bitmap.createScaledBitmap(img, 1, 1, true)
        val color = newImg.getPixel(0, 0)
        newImg.recycle()
        return color
    }

    private fun getImgArt(path: String): ByteArray?{
        val retriever = MediaMetadataRetriever()
        retriever.setDataSource(path)
        return retriever.embeddedPicture
    }
    private fun playMusic(){
        binding.playPause.setImageResource(R.drawable.baseline_pause_24)
        isPlaying = true
        musicService!!.mediaPlayer!!.start()
    }

    private fun pauseMusic(){
        binding.playPause.setImageResource(R.drawable.baseline_play_arrow_24)
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
        createMediaPlayer()
        setLayout()

        MainActivity.binding.songName.isSelected = true

        Glide.with(MainActivity.binding.image)
            .load(musicListPF[songPosition].artUri)
            .apply(RequestOptions().placeholder(R.drawable.baseline_music_off_24).centerCrop())
            .into(MainActivity.binding.image)

        MainActivity.binding.songName.text = musicListPF[songPosition].title
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
        //Toast.makeText(context, "ssdf", Toast.LENGTH_SHORT).show()
        if (musicService != null) {
            MainActivity.binding.root.visibility = View.VISIBLE
            MainActivity.binding.songName.isSelected = true

            Glide.with(requireContext())
                .load(musicListPF[songPosition].artUri)
                .apply(RequestOptions().placeholder(R.drawable.baseline_music_off_24).centerCrop())
                .into(MainActivity.binding.image)

            MainActivity.binding.songName.text = musicListPF[songPosition].title
            MainActivity.binding.artistName.text = musicListPF[songPosition].artist
            if (isPlaying)
                MainActivity.binding.playPause.setImageResource(R.drawable.baseline_pause_24)

            else
                MainActivity.binding.playPause.setImageResource(R.drawable.baseline_play_arrow_24)
        }
    }

}


