package com.example.vibe_audio_player.adapters
import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.vibe_audio_player.R
import com.example.vibe_audio_player.Song
import com.example.vibe_audio_player.databinding.SongItemBinding
import com.example.vibe_audio_player.formatDuration

class SongRVAdapter(
    private val context: Context,
    private var songs: MutableList<Song>,
    private val onItemClicked: (Song, Int) -> Unit
) : RecyclerView.Adapter<SongRVAdapter.SongViewHolder>() {


    class SongViewHolder(val binding: SongItemBinding) : RecyclerView.ViewHolder(binding.root) {
        val title = binding.textView1
        val artist = binding.textView2
        val duration = binding.textView3
        val menu = binding.menu
        val root = binding.root

        fun bind(song: Song, position: Int, onItemClicked: (Song, Int) -> Unit) {
            binding.root.setOnClickListener { onItemClicked(song, position) }
            binding.executePendingBindings()
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
        holder.duration.text = formatDuration(songs[position].duration)

        holder.menu.setOnClickListener{
            Toast.makeText(context, position.toString(), Toast.LENGTH_SHORT).show()
        }

        Glide.with(context)
            .load(songs[position].artUri)
            .apply(RequestOptions().placeholder(R.drawable.baseline_image_not_supported_black_24).centerCrop())
            .into(holder.binding.imageView)

        setAnimation(holder.itemView)
    }



    override fun getItemCount(): Int = songs.size


    @SuppressLint("NotifyDataSetChanged")
    fun updateData(newSongs: ArrayList<Song>) {
        songs.clear()
        songs.addAll(newSongs)
        notifyDataSetChanged()
    }

    private fun setAnimation(view: View) {
        val animation = AnimationUtils.loadAnimation(view.context, R.anim.item_animation)
        view.startAnimation(animation)
    }

    override fun onViewDetachedFromWindow(holder: SongViewHolder) {
        super.onViewDetachedFromWindow(holder)
        holder.itemView.clearAnimation()
    }
}




