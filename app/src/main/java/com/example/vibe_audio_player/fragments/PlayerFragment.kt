package com.example.vibe_audio_player.fragments


/**
 * An example full-screen fragment that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
//class PlayerFragment: Fragment(), ServiceConnection, MediaPlayer.OnCompletionListener{
//
//    companion object{
//        lateinit var musicListPF: ArrayList<Song>
//        var songPosition: Int  = 0
//        var isPlaying: Boolean = false
//        var musicService: MusicService? = null
//        @SuppressLint("StaticFieldLeak")
//        lateinit var binding: FragmentPlayerBinding
//        var nowPlayingId: String = ""
//        var namePlayList: String = ""
//    }
//
//    private val sharedViewModel: SharedViewModel by activityViewModels()
//    private lateinit var songClass: String
//    private lateinit var namePlayLists: String
//    override fun onCreateView(
//        inflater: LayoutInflater, container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View {
//        binding = FragmentPlayerBinding.inflate(inflater, container, false)
//        musicListPF = ArrayList()
//
//        sharedViewModel.songClass.observe(viewLifecycleOwner) { sonп ->
//            songClass = sonп
//        }
//
//        sharedViewModel.songPosition.observe(viewLifecycleOwner) { position ->
//            songPosition = position
//        }
//
//        sharedViewModel.songPosition.observe(viewLifecycleOwner) { PlayList ->
//            namePlayList = PlayList.toString()
//        }
//
//        if(requireActivity().intent.data?.scheme.contentEquals("content")){
//            songPosition = 0
//            val intentService = Intent(requireContext(), MusicService::class.java)
//            requireActivity().bindService(intentService, this, BIND_AUTO_CREATE)
//            requireActivity().startService(intentService)
//            musicListPF = ArrayList()
//            musicListPF.add(getMusicDetails(requireActivity().intent.data!!))
//            Glide.with(requireContext())
//                .load(getImgArt(musicListPF[songPosition].path))
//                .apply(RequestOptions().placeholder(R.drawable.baseline_music_off_24).centerCrop())
//                .into(binding.songImg)
//
//            binding.title.text = musicListPF[songPosition].title
//            binding.artist.text = musicListPF[songPosition].artist
//        }else initializeLayout()
//
//        binding.playPause.setOnClickListener{
//            if (isPlaying)
//                pauseMusic()
//            else
//                playMusic()
//        }
//
//        binding.previous.setOnClickListener{
//            previousOrNextSong(false)
//        }
//
//        binding.next.setOnClickListener{
//            previousOrNextSong(true)
//        }
//
//        binding.seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
//            private var isUserTouching = false
//
//            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
//                if (fromUser) {
//                    isUserTouching = true
//                    // Обновляем только текстовое отображение текущего времени
//                    binding.start.text = formatDuration(progress.toLong())
//                }
//            }
//
//            override fun onStartTrackingTouch(seekBar: SeekBar?) {
//                isUserTouching = true
//            }
//
//            override fun onStopTrackingTouch(seekBar: SeekBar?) {
//                isUserTouching = false
//                // Выполняем seekTo только после завершения перетаскивания
//                seekBar?.progress?.let {
//                    musicService?.mediaPlayer?.seekTo(it)
//                }
//            }
//        })
//
//        binding.back.setOnClickListener{
//           // findNavController().navigate(R.id.action_playerFragment_to_my_music)
//        }
//
//
//
//        return binding.root
//    }
//
////    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
////        super.onViewCreated(view, savedInstanceState)
////
////
////    }
//
//    override fun onResume() {
//        super.onResume()
//        //Toast.makeText(context, "orpa", Toast.LENGTH_SHORT).show()
//        requireActivity().findViewById<View>(R.id.miniPlayer)?.visibility = View.GONE
//    }
//
//    override fun onDestroyView() {
//        super.onDestroyView()
//        //Toast.makeText(context, "odpa", Toast.LENGTH_SHORT).show()
//        requireActivity().findViewById<View>(R.id.miniPlayer)?.visibility = View.VISIBLE
//        updateMiniPlayerUI()
//    }
//
//
//    private fun initializeLayout(){
////        val songClass = arguments?.getString("SONG_CLASS") ?: "Unknown Class"
////        songPosition = arguments?.getInt( "SONG_POSITION") ?: 0
////        val namePlayLists = arguments?.getString("NAME_PLAYLIST") ?: "Unknown Playlist"
//
//        when(songClass){
//            "MyMusic" -> {
//                val intent = Intent(requireContext(), MusicService::class.java)
//                requireActivity().bindService(intent, this, BIND_AUTO_CREATE)
//                requireActivity().startService(intent)
//                musicListPF = ArrayList()
//                musicListPF.addAll(MainActivity.musicListMA)
//                namePlayList = "Мои треки"
//                setLayout()
//
//            }
//            "MiniPlayer" -> {
////                var start = musicService!!.mediaPlayer!!.currentPosition
////                val intent = Intent(requireContext(), MusicService::class.java)
////                requireActivity().bindService(intent, this, BIND_AUTO_CREATE)
////                requireActivity().startService(intent)
//                if (musicListPF.isEmpty())
//                    musicListPF.addAll(MainActivity.musicListMA)
//                setLayout()
////                musicService?.mediaPlayer?.setOnPreparedListener {
////                    it.seekTo(start) // Установка позиции после подготовки
////                    it.start() // Начать воспроизведение
////                    start = 0
////                }
//
//                binding.start.text = formatDuration(musicService!!.mediaPlayer!!.currentPosition.toLong())
//                binding.duration.text = formatDuration(musicService!!.mediaPlayer!!.duration.toLong())
//                binding.seekBar.progress = musicService!!.mediaPlayer!!.currentPosition
//                binding.seekBar.max = musicService!!.mediaPlayer!!.duration
//
//                if (!isPlaying)
//                    binding.playPause.setImageResource(R.drawable.baseline_play_arrow_24)
//            }
//        }
//
//        binding.namePlaylist.text =
//            if (namePlayLists == "")
//                namePlayList
//            else
//                namePlayLists
//
//        if (musicService != null && !isPlaying)
//            playMusic()
//    }
//
//    private fun setLayout(){
//        Glide.with(requireContext().applicationContext)
//            .load(musicListPF[songPosition].artUri)
//            .apply(RequestOptions().placeholder(R.drawable.baseline_music_off_24).centerCrop())
//            .into(binding.songImg)
//
//
//
//        binding.title.text = musicListPF[songPosition].title
//        binding.artist.text = musicListPF[songPosition].artist
//
//
//        val img = getImgArt(musicListPF[songPosition].path)
//        val image = if (img != null){
//            BitmapFactory.decodeByteArray(
//                img, 0, img.size
//            )
//        }else {
//            BitmapFactory.decodeResource(
//                resources, R.drawable.baseline_music_off_24
//            )
//        }
//
//        val bgColor = getMainColor(image)
//        val gradient = GradientDrawable(
//            GradientDrawable.Orientation.BOTTOM_TOP,
//            intArrayOf(0xFFFFFF, bgColor)
//        )
//        binding.root.background = gradient
//        activity?.window?.statusBarColor = bgColor
//    }
//
//
//    private fun createMediaPlayer(){
//        try {
//            if (musicService!!.mediaPlayer == null) musicService!!.mediaPlayer = MediaPlayer()
//            musicService!!.mediaPlayer!!.reset()
//            musicService!!.mediaPlayer!!.setDataSource(musicListPF[songPosition].path)
//            musicService!!.mediaPlayer!!.prepare()
//            binding.start.text = formatDuration(musicService!!.mediaPlayer!!.currentPosition.toLong())
//            binding.duration.text = formatDuration(musicService!!.mediaPlayer!!.duration.toLong())
//            binding.seekBar.progress = 0
//            binding.seekBar.max = musicService!!.mediaPlayer!!.duration
//            musicService!!.mediaPlayer!!.setOnCompletionListener(this)
//            nowPlayingId = musicListPF[songPosition].id
//            playMusic()
//
//        }catch (e: Exception){ Toast.makeText(context, e.toString(), Toast.LENGTH_LONG).show()}
//    }
//
//    private fun getMusicDetails(contentUri: Uri): Song {
//        var cursor: Cursor? = null
//        try {
//            val projection = arrayOf(MediaStore.Audio.Media.DATA, MediaStore.Audio.Media.DURATION)
//            cursor = context?.contentResolver?.query(contentUri, projection, null, null, null)
//            val dataColumn = cursor?.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA)
//            val durationColumn = cursor?.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION)
//            cursor!!.moveToFirst()
//            val path = dataColumn?.let { cursor.getString(it) }
//            val duration = durationColumn?.let { cursor.getLong(it) }!!
//            return Song(id = "Unknown", title = path.toString(), album = "Unknown", artist = "Unknown", duration = duration,
//                artUri = "Unknown", path = path.toString())
//        }finally {
//            cursor?.close()
//        }
//    }
//
//    private fun getMainColor(img: Bitmap): Int {
//        val newImg = Bitmap.createScaledBitmap(img, 1, 1, true)
//        val color = newImg.getPixel(0, 0)
//        newImg.recycle()
//        return color
//    }
//
//    private fun getImgArt(path: String): ByteArray?{
//        val retriever = MediaMetadataRetriever()
//        retriever.setDataSource(path)
//        return retriever.embeddedPicture
//    }
//    private fun playMusic(){
//        binding.playPause.setImageResource(R.drawable.baseline_pause_24)
//        isPlaying = true
//        musicService!!.mediaPlayer!!.start()
//    }
//
//    private fun pauseMusic(){
//        binding.playPause.setImageResource(R.drawable.baseline_play_arrow_24)
//        isPlaying = false
//        musicService!!.mediaPlayer!!.pause()
//    }
//
//    private fun previousOrNextSong(increment: Boolean){
//        if(increment) {
//            setSongPosition(increment = true)
//            setLayout()
//            createMediaPlayer()
//        }
//        else {
//            setSongPosition(increment = false)
//            setLayout()
//            createMediaPlayer()
//        }
//    }
//
//    override fun onCompletion(mp: MediaPlayer?) {
//        setSongPosition(increment = true)
//        createMediaPlayer()
//        setLayout()
//
//        MiniPlayer.binding.songName.isSelected = true
//
//        Glide.with(requireContext().applicationContext)
//            .load(musicListPF[songPosition+1].artUri)
//            .apply(RequestOptions().placeholder(R.drawable.baseline_music_off_24).centerCrop())
//            .into(MiniPlayer.binding.image)
//
//        MiniPlayer.binding.songName.text = musicListPF[songPosition].title
//    }
//
//    override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
//        if(musicService == null){
//            val binder = service as MusicService.MyBinder
//            musicService = binder.currentService()}
//        createMediaPlayer()
//        musicService!!.seekBarSetup()
//    }
//
//    override fun onServiceDisconnected(p0: ComponentName?) {
//        musicService = null
//    }
//
//    override fun onDestroy() {
//        super.onDestroy()
//        if(musicListPF[songPosition].id == "Unknown" && !isPlaying) exitApplication()
//    }
//
//    private fun exitApplication() {
//        if (musicService != null) {
//            musicService!!.mediaPlayer!!.release()
//            musicService = null
//        }
//        exitProcess(1)
//    }
//
//    private fun updateMiniPlayerUI() {
//        //Toast.makeText(context, "ssdf", Toast.LENGTH_SHORT).show()
//        if (musicService != null) {
//            MiniPlayer.binding.root.visibility = View.VISIBLE
//            MiniPlayer.binding.songName.isSelected = true
//
//                Glide.with(requireContext().applicationContext)
//                    .load(musicListPF[songPosition].artUri)
//                    .apply(RequestOptions().placeholder(R.drawable.baseline_music_off_24).centerCrop())
//                    .into(MiniPlayer.binding.image)
//
//            MiniPlayer.binding.songName.text = musicListPF[songPosition].title
//            MiniPlayer.binding.artistName.text = musicListPF[songPosition].artist
//            if (isPlaying)
//                MiniPlayer.binding.playPause.setImageResource(R.drawable.baseline_pause_24)
//
//            else
//                MiniPlayer.binding.playPause.setImageResource(R.drawable.baseline_play_arrow_24)
//        }
//    }
//
//}


