package com.al4gms.unspray.data.modelsui.content

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class PositionGeographic(
    val latitude: Float?,
    val longitude: Float?,
)
