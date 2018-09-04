package net.bonysoft.doityourselfie.photos.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

internal data class SimpleMediaItem(val uploadToken: String)

internal data class NewMediaItem(val description: String,
                                 val simpleMediaItem: SimpleMediaItem)

@Suppress("ArrayInDataClass")
@JsonClass(generateAdapter = true)
internal data class ImageUploadRequest(val albumId: String,
                                       val newMediaItems: Array<NewMediaItem>)

data class Status(val code: Int = 0,
                  val message: String)

data class NewMediaItemResult(val uploadToken: String,
                              val status: Status,
                              val mediaItem: MediaItem)

@Suppress("ArrayInDataClass")
data class ImageUploadResult(val newMediaItemResults: Array<NewMediaItemResult>)

data class Photo(val cameraMake: String = "",
                 val cameraModel: String = "",
                 val focalLength: Float = 0f,
                 val apertureFNumber: Float = 0f,
                 val isoEquivalent: Int = 0)

data class MediaMetaData(val creationTime: String,
                         val width: String,
                         val height: String,
                         val photo: Photo?)

data class MediaItem(val id: String,
                     val description: String?,
                     val productUrl: String,
                     val baseUrl: String,
                     val mimeType: String,
                     val mediaMetadata: MediaMetaData,
                     @Json(name = "filename")
                     val fileName: String)

@Suppress("ArrayInDataClass")
data class PhotoListResponse(val mediaItems: Array<MediaItem>?,
                             val nextPageToken: String?)