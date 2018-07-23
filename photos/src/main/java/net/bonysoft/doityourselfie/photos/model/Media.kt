package net.bonysoft.doityourselfie.photos.model

import com.squareup.moshi.JsonClass

internal data class SimpleMediaItem(val uploadToken: String)

internal data class NewMediaItem(val description: String,
                                 val simpleMediaItem: SimpleMediaItem)

@Suppress("ArrayInDataClass")
@JsonClass(generateAdapter = true)
internal data class ImageUploadRequest(val albumId: String,
                                       val newMediaItems: Array<NewMediaItem>)

