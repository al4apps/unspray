package com.al4gms.unspray.data.db

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.al4gms.unspray.data.modelsdb.content.ContentContract
import com.al4gms.unspray.data.modelsdb.content.ContentDB
import com.al4gms.unspray.data.modelsdb.content.TagContract
import com.al4gms.unspray.data.modelsdb.content.TagDB
import com.al4gms.unspray.data.modelsdb.content.photo.PhotoDbEntities
import kotlinx.coroutines.flow.Flow

@Dao
interface PhotosDao {

    @Query("SELECT * FROM ${ContentContract.PHOTO_TABLE_NAME}")
    fun getPagingSource(): PagingSource<Int, ContentDB.Photo>

    @Query("SELECT * FROM ${ContentContract.PHOTO_TABLE_NAME}")
    fun getPhotos(): Flow<List<ContentDB.Photo>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun savePhotos(photos: List<ContentDB.Photo>)

    @Transaction
    suspend fun savePhotoEntities(dbEntities: List<PhotoDbEntities>) {
        val photos = dbEntities.map { it.photoDB }
        val photoUrls = dbEntities.map { it.photoUrlsDB }
        val locations = dbEntities.mapNotNull { it.locationDB }
        val exifList = dbEntities.mapNotNull { it.exifDB }
        val tags = mutableListOf<TagDB>()
        dbEntities.forEach { tags.plus(it.tagsDB) }

        savePhotos(photos)
//        savePhotoUrls(photoUrls)
        saveTags(tags)
//        saveLocations(locations)
//        saveExifList(exifList)
    }

//    @Insert(onConflict = OnConflictStrategy.REPLACE)
//    suspend fun savePhotoUrls(photoUrls: List<PhotoUrlsDB>)
//
//    @Insert(onConflict = OnConflictStrategy.REPLACE)
//    suspend fun saveExifList(exifList: List<ExifDB>)
//
//    @Insert(onConflict = OnConflictStrategy.REPLACE)
//    suspend fun saveLocations(locations: List<LocationDB>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveTags(tags: List<TagDB>)

    @Transaction
    suspend fun refresh(dbEntities: List<PhotoDbEntities>) {
        clear()
        savePhotoEntities(dbEntities)
    }

    suspend fun clear() {
//        clearExif()
//        clearPhotoUrls()
        clearTags()
//        clearLocations()
        clearPhotos()
    }

    @Query("DELETE FROM ${ContentContract.PHOTO_TABLE_NAME}")
    suspend fun clearPhotos()

//    @Query("DELETE FROM ${PhotoUrlsContracts.TABLE_NAME}")
//    suspend fun clearPhotoUrls()

//    @Query("DELETE FROM ${ExifContract.TABLE_NAME}")
//    suspend fun clearExif()

//    @Query("DELETE FROM ${LocationContract.TABLE_NAME}")
//    suspend fun clearLocations()

    @Query("DELETE FROM ${TagContract.TABLE_NAME}")
    suspend fun clearTags()

//    @Query("SELECT * FROM ${PhotoUrlsContracts.TABLE_NAME} WHERE ${PhotoUrlsContracts.Columns.PHOTO_ID} = :photoId")
//    suspend fun getPhotoUrls(photoId: String): PhotoUrlsDB

//    @Query("SELECT * FROM ${ExifContract.TABLE_NAME} WHERE ${ExifContract.Columns.PHOTO_ID} = :photoId")
//    suspend fun getExif(photoId: String): ExifDB?

//    @Query("SELECT * FROM ${LocationContract.TABLE_NAME} WHERE ${LocationContract.Columns.PHOTO_ID} = :photoId")
//    suspend fun getLocation(photoId: String): LocationDB?

    @Query("SELECT * FROM ${TagContract.TABLE_NAME} WHERE ${TagContract.Columns.PHOTO_ID} = :photoId")
    suspend fun getTags(photoId: String): List<TagDB>
}
