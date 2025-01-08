
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

class SongRVAdapter(
    private val context: Context,
    private val songs: ArrayList<Song>,
    private val onItemClicked: (Song) -> Unit
) : RecyclerView.Adapter<SongRVAdapter.SongViewHolder>() {

    // ViewHolder для работы с song_item.xml
    class SongViewHolder(val binding: SongItemBinding) : RecyclerView.ViewHolder(binding.root) {
//        val title = binding.textView1
//        val albom = binding.textView2
//        val image = binding.imageView
//        val duration = binding.textView3

        fun bind(song: Song, onItemClicked: (Song) -> Unit) {
            binding.song = song // Устанавливаем переменную song в макете
            binding.root.setOnClickListener { onItemClicked(song) }
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
        holder.bind(songs[position], onItemClicked)
//        holder.title.text = songs[position].title
//        holder.albom.text = songs[position].album
//        holder.duration.text = songs[position].duration.toString()
        Glide.with(context)
            .load(songs[position].artUri)
            .apply(RequestOptions().placeholder(R.drawable.baseline_music_off_24).centerCrop())
            .into(holder.binding.imageView)

    }

    override fun getItemCount(): Int = songs.size
}
