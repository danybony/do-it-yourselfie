package net.bonysoft.doityourselfie.queue

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey

@Entity
class Picture {

    @PrimaryKey
    @ColumnInfo(name = "image_url")
    lateinit var imageUrl: String

    @ColumnInfo(name = "uploaded")
    var uploaded: Int = 0
}
