package com.al4gms.unspray.data.modelsui.content

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class PhotoUrls(
    val raw: String,
    val full: String,
    val regular: String,
    val small: String,
    val thumb: String,
    @Json(name = "small_s3")
    val smallS3: String,
)

// "raw":"https://images.unsplash.com/photo-1679327970394-8593ba86023e?ixid=Mnw0MjM0MTd8MHwxfGFsbHwxfHx8fHx8Mnx8MTY3OTM5MzU3Ng\u0026ixlib=rb-4.0.3",
// "full":"https://images.unsplash.com/photo-1679327970394-8593ba86023e?crop=entropy\u0026cs=tinysrgb\u0026fm=jpg\u0026ixid=Mnw0MjM0MTd8MHwxfGFsbHwxfHx8fHx8Mnx8MTY3OTM5MzU3Ng\u0026ixlib=rb-4.0.3\u0026q=80",
// "regular":"https://images.unsplash.com/photo-1679327970394-8593ba86023e?crop=entropy\u0026cs=tinysrgb\u0026fit=max\u0026fm=jpg\u0026ixid=Mnw0MjM0MTd8MHwxfGFsbHwxfHx8fHx8Mnx8MTY3OTM5MzU3Ng\u0026ixlib=rb-4.0.3\u0026q=80\u0026w=1080",
// "small":"https://images.unsplash.com/photo-1679327970394-8593ba86023e?crop=entropy\u0026cs=tinysrgb\u0026fit=max\u0026fm=jpg\u0026ixid=Mnw0MjM0MTd8MHwxfGFsbHwxfHx8fHx8Mnx8MTY3OTM5MzU3Ng\u0026ixlib=rb-4.0.3\u0026q=80\u0026w=400",
// "thumb":"https://images.unsplash.com/photo-1679327970394-8593ba86023e?crop=entropy\u0026cs=tinysrgb\u0026fit=max\u0026fm=jpg\u0026ixid=Mnw0MjM0MTd8MHwxfGFsbHwxfHx8fHx8Mnx8MTY3OTM5MzU3Ng\u0026ixlib=rb-4.0.3\u0026q=80\u0026w=200",
// "small_s3":"https://s3.us-west-2.amazonaws.com/images.unsplash.com/small/photo-1679327970394-8593ba86023e"
