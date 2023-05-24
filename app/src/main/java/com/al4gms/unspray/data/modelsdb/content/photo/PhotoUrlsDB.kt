package com.al4gms.unspray.data.modelsdb.content.photo

import androidx.room.ColumnInfo

data class PhotoUrlsDB(
    @ColumnInfo(name = PhotoUrlsContracts.Columns.RAW)
    val raw: String,
    @ColumnInfo(name = PhotoUrlsContracts.Columns.FULL)
    val full: String,
    @ColumnInfo(name = PhotoUrlsContracts.Columns.REGULAR)
    val regular: String,
    @ColumnInfo(name = PhotoUrlsContracts.Columns.SMALL)
    val small: String,
    @ColumnInfo(name = PhotoUrlsContracts.Columns.THUMB)
    val thumb: String,
    @ColumnInfo(name = PhotoUrlsContracts.Columns.SMALL_S3)
    val smallS3: String,
)
