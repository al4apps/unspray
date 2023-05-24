package com.al4gms.unspray.data.modelsdb.content

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = TagContract.TABLE_NAME,
    foreignKeys = [
        ForeignKey(
            entity = ContentDB.Photo::class,
            parentColumns = [ContentContract.PhotosColumn.ID],
            childColumns = [TagContract.Columns.PHOTO_ID],
        ),
    ],
    indices = [
        Index(TagContract.Columns.PHOTO_ID, unique = true),
    ],
)
data class TagDB(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = TagContract.Columns.ID)
    val id: Long = 0L,
    @ColumnInfo(name = TagContract.Columns.TYPE)
    val type: String,
    @ColumnInfo(name = TagContract.Columns.PHOTO_ID)
    val photoId: String,
    @ColumnInfo(name = TagContract.Columns.TITLE)
    val title: String,
)
