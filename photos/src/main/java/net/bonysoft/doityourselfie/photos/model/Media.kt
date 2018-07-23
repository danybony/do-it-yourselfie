package net.bonysoft.doityourselfie.photos.model

import com.squareup.moshi.JsonClass

internal data class SimpleMediaItem(val uploadToken: String)

internal data class NewMediaItem(val description: String,
                                 val simpleMediaItem: SimpleMediaItem)

@Suppress("ArrayInDataClass")
@JsonClass(generateAdapter = true)
internal data class ImageUploadRequest(val albumId: String,
                                       val newMediaItems: Array<NewMediaItem>)

data class Status(val code: Int,
                  val message: String)

data class NewMediaItemResult(val uploadToken: String,
                              val status: Status)

@Suppress("ArrayInDataClass")
data class ImageUploadResult(val newMediaItemResults: Array<NewMediaItemResult>)
