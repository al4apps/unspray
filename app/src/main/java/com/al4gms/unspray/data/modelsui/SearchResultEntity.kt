package com.al4gms.unspray.data.modelsui

import com.al4gms.unspray.data.modelsui.content.Content
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class SearchResultEntity(
    val total: Int,
    @Json(name = "total_pages")
    val totalPages: Int,
    val results: List<Content.Photo>,
)
