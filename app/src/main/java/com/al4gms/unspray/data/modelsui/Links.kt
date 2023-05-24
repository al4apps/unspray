package com.al4gms.unspray.data.modelsui

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Links(
    @Json(name = "download_location")
    val downloadLocation: String,
)

/**
"self": "https://api.unsplash.com/photos/LBI7cgq3pbM",
"html": "https://unsplash.com/photos/LBI7cgq3pbM",
"download": "https://unsplash.com/photos/LBI7cgq3pbM/download", // don't use this property
"download_location": "https://api.unsplash.com/photos/LBI7cgq3pbM/download?ixid=MnwxMTc4ODl8MHwxfHNlYXJjaHwxfHxwdXBweXxlbnwwfHx8fDE2MTc3NTA2MTM" // use this one ;)

 */
