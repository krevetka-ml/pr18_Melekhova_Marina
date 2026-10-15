package com.example.pr18_melekhovamarina

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class DiscAdapter(
    private var items: List<Disc>,
    private val onDelete: (Disc) -> Unit
) : RecyclerView.Adapter<DiscAdapter.DiscHolder>() {

    class DiscHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvTitle: TextView = view.findViewById(R.id.tv_disc_title)
        val tvArtist: TextView = view.findViewById(R.id.tv_disc_artist)
        val tvInfo: TextView = view.findViewById(R.id.tv_disc_info)
        val tvFavorite: TextView = view.findViewById(R.id.tv_favorite)
        val btnDelete: ImageButton = view.findViewById(R.id.btn_delete)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DiscHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_disc, parent, false)
        return DiscHolder(view)
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: DiscHolder, position: Int) {
        val disc = items[position]
        holder.tvTitle.text = disc.title
        holder.tvArtist.text = disc.artist
        holder.tvInfo.text =
            "${disc.year}  •  ${disc.genre}  •  ${disc.tracks} ${trackWord(disc.tracks)}"
        holder.tvFavorite.visibility = if (disc.favorite) View.VISIBLE else View.GONE
        holder.btnDelete.setOnClickListener { onDelete(disc) }
    }

    fun submit(newItems: List<Disc>) {
        items = newItems
        notifyDataSetChanged()
    }

    private fun trackWord(n: Int): String = when {
        n % 100 in 11..14 -> "треков"
        n % 10 == 1 -> "трек"
        n % 10 in 2..4 -> "трека"
        else -> "треков"
    }
}