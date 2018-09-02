package net.bonysoft.doityourselfie.queue

import android.arch.persistence.room.Database
import android.arch.persistence.room.RoomDatabase

@Database(entities = [(Picture::class)], version = 1)
abstract class QueueDatabase : RoomDatabase() {

    abstract fun pictureDao(): PictureDao

}
