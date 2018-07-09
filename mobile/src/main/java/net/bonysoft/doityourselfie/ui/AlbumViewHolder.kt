package net.bonysoft.doityourselfie.ui

import android.support.v7.widget.RecyclerView
import android.view.View
import net.bonysoft.doityourselfie.R
import net.bonysoft.doityourselfie.imageView
import net.bonysoft.doityourselfie.photos.model.CompleteAlbum
import net.bonysoft.doityourselfie.textView

class AlbumViewHolder(itemView: View,
                      private val listener: AlbumSelectedListener) : RecyclerView.ViewHolder(itemView) {

    private val titleView = itemView.textView(R.id.albumTitle)
    private val urlView = itemView.textView(R.id.productUrl)
    private val sizeView = itemView.textView(R.id.numberOfPictures)
    private val coverView = itemView.imageView(R.id.albumCover)

    fun bindTo(album: CompleteAlbum) {
        itemView.setOnClickListener {
            listener.onAlbumSelected(album)
        }

        //TODO bind cover

        titleView.text = album.title
        urlView.text = album.productUrl
        sizeView.text = "${album.totalMediaItems}"
    }
}