package com.al4gms.unspray.data.modelsui.user

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ProfileImageUrls(
    val small: String,
    val medium: String,
    val large: String,
)
