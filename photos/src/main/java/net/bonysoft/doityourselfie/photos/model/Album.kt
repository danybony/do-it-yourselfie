package net.bonysoft.doityourselfie.photos.model

import android.os.Parcelable
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.android.parcel.Parcelize


internal data class Album(val title: String)

@JsonClass(generateAdapter = true)
internal data class AlbumRequest(val album: Album)

data class AlbumResponse(
        val productUrl: String,
        val id: String,
        val title: String,
        @Json(name = "isWriteable")
        val writeable: String
)

@Parcelize
data class CompleteAlbum(
        val id: String,
        val title: String,
        val productUrl: String,
        val coverPhotoBaseUrl: String,
        val isWriteable: String,
        val totalMediaItems: Int
) : Parcelable

@Suppress("ArrayInDataClass")
data class AlbumListResponse(
        val albums: Array<CompleteAlbum>,
        val nextPageToken: String?
)