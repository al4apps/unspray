package com.al4gms.unspray.data.modelsdb.content

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.al4gms.unspray.data.modelsdb.content.photo.ExifDB
import com.al4gms.unspray.data.modelsdb.content.photo.LocationDB
import com.al4gms.unspray.data.modelsdb.content.photo.PhotoUrlsDB
import com.al4gms.unspray.data.modelsdb.user.UserContract
import com.al4gms.unspray.data.modelsdb.user.UserDB
import com.squareup.moshi.JsonClass

sealed class ContentDB {
    @JsonClass(generateAdapter = true)
    @Entity(
        tableName = ContentContract.COLLECTION_TABLE_NAME,
        foreignKeys = [
            ForeignKey(
                entity = UserDB::class,
                parentColumns = [UserContract.Columns.ID],
                childColumns = [ContentContract.CollectionColumn.USER_ID],
            ),
        ],
    )
    data class Collection(
        @PrimaryKey
        @ColumnInfo(name = ContentContract.CollectionColumn.ID)
        val id: String,
        @ColumnInfo(name = ContentContract.CollectionColumn.TITLE)
        val title: String,
        @ColumnInfo(name = ContentContract.CollectionColumn.DESCRIPTION)
        val description: String?,
        @ColumnInfo(name = ContentContract.CollectionColumn.TOTAL_PHOTOS)
        val totalPhotos: Int,
        @ColumnInfo(name = ContentContract.CollectionColumn.PRIVATE)
        val private: Boolean,
        @ColumnInfo(name = ContentContract.CollectionColumn.USER_ID)
        val userId: String,
    ) : ContentDB()

    @Entity(
        tableName = ContentContract.PHOTO_TABLE_NAME,
    )
    data class Photo(
        @PrimaryKey
        @ColumnInfo(name = ContentContract.PhotosColumn.ID)
        val id: String,
        @ColumnInfo(name = ContentContract.PhotosColumn.CREATED_AT)
        val createdAt: String,
        @ColumnInfo(name = ContentContract.PhotosColumn.WIDTH)
        val width: Int,
        @ColumnInfo(name = ContentContract.PhotosColumn.HEIGHT)
        val height: Int,
        @ColumnInfo(name = ContentContract.PhotosColumn.BLUR_HASH)
        val blurHash: String,
        @ColumnInfo(name = ContentContract.PhotosColumn.DOWNLOADS)
        val downloads: Int?,
        @ColumnInfo(name = ContentContract.PhotosColumn.LIKES)
        val likes: Int,
        @ColumnInfo(name = ContentContract.PhotosColumn.LIKED_BY_USER)
        val likedByUser: Boolean,
        @ColumnInfo(name = ContentContract.PhotosColumn.USER_ID)
        val userId: String,
        @ColumnInfo(name = ContentContract.PhotosColumn.DESCRIPTION)
        val description: String?,
        @ColumnInfo(name = ContentContract.PhotosColumn.DOWNLOAD_LINK)
        val downloadLink: String,
        @Embedded
        val photoUrls: PhotoUrlsDB,
        @Embedded
        val exif: ExifDB?,
        @Embedded
        val location: LocationDB?,
    ) : ContentDB()
}
