package com.al4gms.unspray.data.modelsui.content

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Exif(
    val make: String?,
    val model: String?,
    val name: String?,
    @Json(name = "exposure_time")
    val exposureTime: String?,
    val aperture: Float?,
    @Json(name = "focal_length")
    val focalLength: String?,
    val iso: Int?,
)

//        "exif": {
//        "make": "Canon",
//        "model": "Canon EOS 40D",
//        "name": "Canon, EOS 40D",
//        "exposure_time": "0.011111111111111112",
//        "aperture": "4.970854",
//        "focal_length": "37",
//        "iso": 100
//    },
