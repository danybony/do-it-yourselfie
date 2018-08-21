package net.bonysoft.doityourselfie.queue

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.Query
import android.arch.persistence.room.Update

@Dao
interface PictureDao {

    @Query("SELECT * FROM picture WHERE uploaded = 0")
    fun queuedPictures(): List<Picture>

    @Insert
    fun put(picture: Picture)

    @Update
    fun update(picture: Picture)
}
