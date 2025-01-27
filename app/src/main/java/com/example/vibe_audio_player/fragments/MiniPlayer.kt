package com.example.vibe_audio_player.fragments


//class MiniPlayer : Fragment() {
//
//    companion object{
//        @SuppressLint("StaticFieldLeak")
//        lateinit var binding: FragmentMiniPlayerBinding
//    }
//    private lateinit var resultLauncher: ActivityResultLauncher<Intent>
//
//    override fun onCreateView(
//        inflater: LayoutInflater, container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View? {
//        binding = FragmentMiniPlayerBinding.inflate(inflater, container, false)
//
//        binding.root.visibility = View.INVISIBLE
//
//        binding.playPause.setOnClickListener{
//            if (PlayerActivity.isPlaying)
//                pauseMusic()
//            else
//                playMusic()
//        }
//
//        binding.next.setOnClickListener{
//            setSongPosition(increment = true)
//            PlayerActivity.musicService!!.createMediaPlayer()
//            Glide.with(requireContext())
//                .load(musicListPA[songPosition].artUri)
//                .apply(RequestOptions().placeholder(R.drawable.baseline_music_off_24).centerCrop())
//                .into(binding.image)
//
//            binding.songName.text = musicListPA[songPosition].title
//            binding.artistName.text = musicListPA[songPosition].artist
//            playMusic()
//        }
//
//        binding.root.setOnClickListener{
//            val intent = Intent(context, PlayerActivity::class.java).apply {
//                putExtra("position", songPosition)
//                putExtra("song_class", "MiniPlayer")
//                putExtra("namePlayList", "")
//            }
//            resultLauncher.launch(intent)
//        }
//
//
//        return binding.root
//    }
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
//            if (result.resultCode == Activity.RESULT_OK) {
//                val bundle = Bundle().apply {
//                    putString("artist", result.data?.getStringExtra("artist"))
//                }
//
//                try {
//                    findNavController().navigate(R.id.action_my_music_to_aboutArtistFragment, bundle)
//                }catch (_: Exception){}
//
//
//            }
//        }
//    }
//
//    override fun onResume() {
//        super.onResume()
//        if (PlayerActivity.musicService != null) {
//            binding.root.visibility = View.VISIBLE
//            binding.songName.isSelected = true
//            Glide.with(requireContext())
//                .load(musicListPA[songPosition].artUri)
//                .apply(RequestOptions().placeholder(R.drawable.baseline_music_off_24).centerCrop())
//                .into(binding.image)
//            binding.songName.text = musicListPA[songPosition].title
//            binding.artistName.text = musicListPA[songPosition].artist
//            if (PlayerActivity.isPlaying)
//                binding.playPause.setImageResource(R.drawable.baseline_pause_24)
//
//            else
//                binding.playPause.setImageResource(R.drawable.baseline_play_arrow_24)
//        }
//    }
//
//    private fun playMusic(){
//        PlayerActivity.isPlaying = true
//        PlayerActivity.musicService!!.mediaPlayer!!.start()
//        binding.playPause.setImageResource(R.drawable.baseline_pause_24)
//        PlayerActivity.binding.next.setImageResource(R.drawable.baseline_pause_24)
//
//    }
//
//    private fun pauseMusic(){
//        PlayerActivity.isPlaying = false
//        PlayerActivity.musicService!!.mediaPlayer!!.pause()
//        binding.playPause.setImageResource(R.drawable.baseline_play_arrow_24)
//        PlayerActivity.binding.next.setImageResource(R.drawable.baseline_play_arrow_24)
//
//    }
//}