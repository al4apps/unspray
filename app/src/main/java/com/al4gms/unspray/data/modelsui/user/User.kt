package com.al4gms.unspray.data.modelsui.user

import com.al4gms.unspray.data.modelsdb.user.UserDB
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class User(
    val id: String,
    val username: String,
    val name: String,
    val bio: String?,
    @Json(name = "profile_image")
    val profileImage: ProfileImageUrls,
    val location: String?,
    @Json(name = "total_likes")
    val totalLikes: Int,
    @Json(name = "total_photos")
    val totalPhotos: Int,
    @Json(name = "total_collections")
    val totalCollections: Int,
    @Json(name = "instagram_username")
    val instagramUsername: String?,
    val email: String?,
    val downloads: Int?,
) {
    fun toUserDb(): UserDB {
        return UserDB(
            id = this.id,
            username = username,
            name = name,
            bio = bio,
            location = location,
            totalLikes = totalLikes,
            totalPhotos = totalPhotos,
            totalCollections = totalCollections,
            instagramUsername = instagramUsername,
            email = email,
            downloads = downloads,
            profileImageSmall = profileImage.small,
            profileImageMedium = profileImage.medium,
            profileImageLarge = profileImage.large,
        )
    }
}
//
// "id": "pXhwzz1JtQU",
// "username": "poorkane",
// "name": "Gilbert Kane",
// "portfolio_url": "https://theylooklikeeggsorsomething.com/",
// "bio": "XO",
// "location": "Way out there",
// "total_likes": 5,
// "total_photos": 74,
// "total_collections": 52,
// "instagram_username": "instantgrammer",
// "twitter_username": "crew",
// "profile_image": {
//    "small": "https://images.unsplash.com/face-springmorning.jpg?q=80&fm=jpg&crop=faces&fit=crop&h=32&w=32",
//    "medium": "https://images.unsplash.com/face-springmorning.jpg?q=80&fm=jpg&crop=faces&fit=crop&h=64&w=64",
//    "large": "https://images.unsplash.com/face-springmorning.jpg?q=80&fm=jpg&crop=faces&fit=crop&h=128&w=128"
