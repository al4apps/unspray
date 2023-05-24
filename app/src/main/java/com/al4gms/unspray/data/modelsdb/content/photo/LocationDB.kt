package com.al4gms.unspray.data.modelsdb.content.photo

import androidx.room.ColumnInfo

data class LocationDB(
    @ColumnInfo(name = LocationContract.Columns.CITY)
    val city: String?,
    @ColumnInfo(name = LocationContract.Columns.COUNTRY)
    val country: String?,
    @ColumnInfo(name = LocationContract.Columns.LATITUDE)
    val latitude: Float?,
    @ColumnInfo(name = LocationContract.Columns.LONGITUDE)
    val longitude: Float?,
)
