
import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.vibe_audio_player.R
import com.example.vibe_audio_player.Song
import com.example.vibe_audio_player.databinding.SongItemBinding
import com.example.vibe_audio_player.formatDuration

class SongRVAdapter(
    private val context: Context,
    private val songs: MutableList<Song>,
    private val onItemClicked: (Song, Int) -> Unit
) : RecyclerView.Adapter<SongRVAdapter.SongViewHolder>() {

    // ViewHolder для работы с song_item.xml
    class SongViewHolder(val binding: SongItemBinding) : RecyclerView.ViewHolder(binding.root) {
        val title = binding.textView1
        val artist = binding.textView2
        val duration = binding.textView3

        fun bind(song: Song, position: Int, onItemClicked: (Song, Int) -> Unit) {
//            binding.song = song // Устанавливаем переменную song в макете
            binding.root.setOnClickListener { onItemClicked(song, position) }
            binding.executePendingBindings() // Применяем изменения



        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SongViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = SongItemBinding.inflate(inflater, parent, false)
        return SongViewHolder(binding)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: SongViewHolder, position: Int) {
        holder.bind(songs[position], position, onItemClicked)
        holder.title.text = songs[position].title
        holder.artist.text = songs[position].artist
        holder.duration.text = formatDuration( songs[position].duration)
        Glide.with(context)
            .load(songs[position].artUri)
            .apply(RequestOptions().placeholder(R.drawable.baseline_music_off_24).centerCrop())
            .into(holder.binding.imageView)

    }

    override fun getItemCount(): Int = songs.size
}
