package com.al4gms.unspray.data.modelsdb.user

object UserContract {

    const val TABLE_NAME = "user"

    object Columns {
        const val ID = "id"
        const val USERNAME = "username"
        const val NAME = "name"
        const val BIO = "bio"
        const val PROFILE_IMAGE_ID = "profile_image_id"
        const val LOCATION = "location"
        const val TOTAL_LIKES = "total_likes"
        const val TOTAL_PHOTOS = "total_photos"
        const val TOTAL_COLLECTIONS = "total_collections"
        const val INSTAGRAM_USERNAME = "instagram_username"
        const val EMAIL = "email"
        const val DOWNLOADS = "downloads"
        const val PROFILE_IMAGE_SMALL = "profile_image_small"
        const val PROFILE_IMAGE_MEDIUM = "profile_image_medium"
        const val PROFILE_IMAGE_LARGE = "profile_image_large"
    }
}
