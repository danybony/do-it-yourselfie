package net.bonysoft.doityourselfie.ui

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import net.bonysoft.doityourselfie.R
import net.bonysoft.doityourselfie.photos.model.MediaItem

class PhotoAdapter(context: Context) : RecyclerView.Adapter<PhotoViewHolder>() {

    private val inflater = LayoutInflater.from(context)
    private val items = arrayListOf<MediaItem>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhotoViewHolder =
            PhotoViewHolder(inflater.inflate(R.layout.photo_item, parent, false))

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: PhotoViewHolder, position: Int) {
        holder.bindTo(items[position])
    }

    fun addPhotos(newItems: ArrayList<MediaItem>) {
        items.let {
            it.clear()
            it.addAll(newItems)
        }

        notifyDataSetChanged()
    }

}