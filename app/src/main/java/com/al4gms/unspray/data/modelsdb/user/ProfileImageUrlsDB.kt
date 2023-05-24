package com.al4gms.unspray.data.modelsdb.user

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
@Entity(
    tableName = ProfileImageUrlsContract.TABLE_NAME,
    foreignKeys = [
        ForeignKey(
            entity = UserDB::class,
            parentColumns = [UserContract.Columns.ID],
            childColumns = [ProfileImageUrlsContract.Columns.USER_ID],
        ),
    ],
    indices = [
        Index(ProfileImageUrlsContract.Columns.USER_ID, unique = true),
    ],
)
data class ProfileImageUrlsDB(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = ProfileImageUrlsContract.Columns.ID)
    val id: Long = 0L,
    @ColumnInfo(name = ProfileImageUrlsContract.Columns.SMALL)
    val small: String,
    @ColumnInfo(name = ProfileImageUrlsContract.Columns.USER_ID)
    val userId: String,
    @ColumnInfo(name = ProfileImageUrlsContract.Columns.MEDIUM)
    val medium: String,
    @ColumnInfo(name = ProfileImageUrlsContract.Columns.LARGE)
    val large: String,
)
