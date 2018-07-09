package net.bonysoft.doityourselfie.ui

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import net.bonysoft.doityourselfie.R
import net.bonysoft.doityourselfie.photos.model.CompleteAlbum

class AlbumAdapter(context: Context,
                   private val listener: AlbumSelectedListener) : RecyclerView.Adapter<AlbumViewHolder>() {

    private val inflater = LayoutInflater.from(context)
    private val albums = arrayListOf<CompleteAlbum>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
            AlbumViewHolder(inflater.inflate(R.layout.album_item, parent, false), listener)

    override fun getItemCount(): Int = albums.size

    override fun onBindViewHolder(holder: AlbumViewHolder, position: Int) {
        holder.bindTo(albums[position])
    }

    fun addAlbums(newAlbums: ArrayList<CompleteAlbum>) {
        albums.let {
            it.clear()
            it.addAll(newAlbums)
        }

        notifyDataSetChanged()
    }

}