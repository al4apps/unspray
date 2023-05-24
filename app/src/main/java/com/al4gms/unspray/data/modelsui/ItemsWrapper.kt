package com.al4gms.unspray.data.modelsui

import com.al4gms.unspray.data.modelsui.content.Content
import com.al4gms.unspray.data.modelsui.user.User
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ItemsWrapper(
    val photo: Content.Photo,
    val user: User,
)
