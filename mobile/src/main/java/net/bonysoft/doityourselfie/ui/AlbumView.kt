package net.bonysoft.doityourselfie.ui

import android.content.Context
import android.support.v7.widget.CardView
import android.util.AttributeSet
import net.bonysoft.doityourselfie.EMTPY_ALBUM_RESPONSE
import net.bonysoft.doityourselfie.R
import net.bonysoft.doityourselfie.lazyTextView
import net.bonysoft.doityourselfie.photos.model.AlbumResponse
import net.bonysoft.doityourselfie.toCompleteAlbum
import kotlin.properties.Delegates

class AlbumView @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : CardView(context, attrs, defStyleAttr) {

    private val titleView by lazyTextView(R.id.albumTitle)
    private val productUrlView by lazyTextView(R.id.productUrl)
    private val writeableView by lazyTextView(R.id.isWriteable)

    private var album: AlbumResponse by Delegates.observable(EMTPY_ALBUM_RESPONSE) { _, _, new ->
        titleView.text = new.title
        productUrlView.text = new.productUrl
        writeableView.text = new.writeable
    }

    init {
        inflate(context, R.layout.album_created_view, this)
    }

    fun bindTo(albumResponse: AlbumResponse) {
        album = albumResponse
    }

    fun setListener(listener: AlbumSelectedListener) {
        setOnClickListener {
            listener.onAlbumSelected(album.toCompleteAlbum())
        }
    }
}