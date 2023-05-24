package com.al4gms.unspray.data.modelsdb.content.photo

import androidx.room.ColumnInfo

data class ExifDB(
    @ColumnInfo(name = ExifContract.Columns.MAKE)
    val make: String?,
    @ColumnInfo(name = ExifContract.Columns.MODEL)
    val model: String?,
    @ColumnInfo(name = ExifContract.Columns.NAME)
    val name: String?,
    @ColumnInfo(name = ExifContract.Columns.EXPOSURE_TIME)
    val exposureTime: String?,
    @ColumnInfo(name = ExifContract.Columns.APERTURE)
    val aperture: Float?,
    @ColumnInfo(name = ExifContract.Columns.FOCAL_LENGTH)
    val focalLength: String?,
    @ColumnInfo(name = ExifContract.Columns.ISO)
    val iso: Int?,
)
