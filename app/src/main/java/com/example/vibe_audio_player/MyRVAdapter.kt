import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.vibe_audio_player.Song
import com.example.vibe_audio_player.databinding.SongItemBinding

class SongRVAdapter(
    private val songs: List<Song>,
    private val onItemClicked: (Song) -> Unit
) : RecyclerView.Adapter<SongRVAdapter.SongViewHolder>() {

    // ViewHolder для работы с song_item.xml
    class SongViewHolder(private val binding: SongItemBinding) : RecyclerView.ViewHolder(binding.root) {
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

    override fun onBindViewHolder(holder: SongViewHolder, position: Int) {
        holder.bind(songs[position], onItemClicked)
    }

    override fun getItemCount(): Int = songs.size
}
