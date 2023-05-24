package com.al4gms.unspray.data.modelsdb.user

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(
    tableName = UserContract.TABLE_NAME,
)
data class UserDB(
    @PrimaryKey
    @ColumnInfo(name = UserContract.Columns.ID)
    val id: String,
    @ColumnInfo(name = UserContract.Columns.USERNAME)
    val username: String,
    @ColumnInfo(name = UserContract.Columns.NAME)
    val name: String,
    @ColumnInfo(name = UserContract.Columns.BIO)
    val bio: String?,
    @ColumnInfo(name = UserContract.Columns.LOCATION)
    val location: String?,
    @ColumnInfo(name = UserContract.Columns.TOTAL_LIKES)
    val totalLikes: Int,
    @ColumnInfo(name = UserContract.Columns.TOTAL_PHOTOS)
    val totalPhotos: Int,
    @ColumnInfo(name = UserContract.Columns.TOTAL_COLLECTIONS)
    val totalCollections: Int,
    @ColumnInfo(name = UserContract.Columns.INSTAGRAM_USERNAME)
    val instagramUsername: String?,
    @ColumnInfo(name = UserContract.Columns.EMAIL)
    val email: String?,
    @ColumnInfo(name = UserContract.Columns.DOWNLOADS)
    val downloads: Int?,
    @ColumnInfo(name = UserContract.Columns.PROFILE_IMAGE_SMALL)
    val profileImageSmall: String,
    @ColumnInfo(name = UserContract.Columns.PROFILE_IMAGE_MEDIUM)
    val profileImageMedium: String,
    @ColumnInfo(name = UserContract.Columns.PROFILE_IMAGE_LARGE)
    val profileImageLarge: String,
)
