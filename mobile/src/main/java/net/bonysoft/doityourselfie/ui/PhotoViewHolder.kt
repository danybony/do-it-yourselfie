package net.bonysoft.doityourselfie.ui

import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.ImageView
import com.squareup.picasso.Picasso
import net.bonysoft.doityourselfie.R
import net.bonysoft.doityourselfie.fetchImageOfSize
import net.bonysoft.doityourselfie.photos.model.MediaItem
import net.bonysoft.doityourselfie.toPixel

class PhotoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    private lateinit var mediaItem: MediaItem

    fun bindTo(mediaItem: MediaItem) {
        this.mediaItem = mediaItem
        val size = 128.toPixel(itemView.resources)
        Picasso.get()
                .load(mediaItem.fetchImageOfSize(size))
                .resize(size, size)
                .centerInside()
                .placeholder(R.drawable.ic_photo_placeholder)
                .into(itemView as ImageView)
    }
}