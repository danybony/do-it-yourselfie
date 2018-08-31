package net.bonysoft.doityourselfie.ui

import android.content.Context
import android.support.constraint.ConstraintLayout
import android.util.AttributeSet
import net.bonysoft.doityourselfie.*
import net.bonysoft.doityourselfie.photos.model.AlbumResponse
import kotlin.properties.Delegates

class AlbumView @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {

    private val titleView by lazyTextView(R.id.albumTitle)
    private val productUrlView by lazyTextView(R.id.productUrl)
    private val writeableView by lazyTextView(R.id.isWriteable)
    private val contentView by lazyView(R.id.rootView)

    private var album: AlbumResponse by Delegates.observable(EMPTY_ALBUM_RESPONSE) { _, _, new ->
        titleView.text = new.title
        productUrlView.text = new.productUrl
        writeableView.text = new.writeable.toString()
    }

    init {
        inflate(context, R.layout.album_created_view, this)
    }

    fun bindTo(albumResponse: AlbumResponse) {
        album = albumResponse
    }

    fun setListener(listener: AlbumSelectedListener) {
        contentView.setOnClickListener {
            listener.onAlbumSelected(album.toCompleteAlbum())
        }
    }
}