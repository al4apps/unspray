package com.al4gms.unspray.data.modelsui.content

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Location(
    val city: String?,
    val country: String?,
    val position: PositionGeographic?,
)

//        "location": {
//        "city": "Montreal",
//        "country": "Canada",
//        "position": {
//            "latitude": 45.473298,
//            "longitude": -73.638488
//        }
//    },
