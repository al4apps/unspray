package com.al4gms.unspray.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.al4gms.unspray.data.modelsdb.content.ContentDB
import com.al4gms.unspray.data.modelsdb.content.TagDB
import com.al4gms.unspray.data.modelsdb.user.UserDB

@Database(
    entities = [
        UserDB::class,
        ContentDB.Photo::class,
        TagDB::class,
    ],
    version = UnsprayDatabase.DB_VERSION,
)
abstract class UnsprayDatabase : RoomDatabase() {

    abstract fun userDao(): UserDao
    abstract fun photosDao(): PhotosDao
    abstract fun collectionsDao(): CollectionsDao

    companion object {
        const val DB_VERSION = 1
        const val DB_NAME = "unspray_database"
    }
}
