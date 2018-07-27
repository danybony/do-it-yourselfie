package net.bonysoft.doityourselfie.ui

import android.support.v7.widget.RecyclerView
import android.view.View
import com.squareup.picasso.Picasso
import net.bonysoft.doityourselfie.*
import net.bonysoft.doityourselfie.photos.model.CompleteAlbum

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

        if (album.coverPhotoBaseUrl.isNotEmpty()) {
            val size = 98.toPixel(itemView.resources)
            Picasso.get()
                    .load(album.fetchImageOfSize(size))
                    .resize(size, size)
                    .centerInside()
                    .placeholder(R.drawable.ic_photo_placeholder)
                    .into(coverView)
        } else {
            coverView.setImageResource(R.drawable.ic_photo_placeholder)
        }

        titleView.text = album.title
        urlView.text = album.productUrl
        sizeView.text = "${album.totalMediaItems}"
    }
}