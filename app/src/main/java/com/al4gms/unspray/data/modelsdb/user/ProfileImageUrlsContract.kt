package com.al4gms.unspray.data.modelsdb.user

object ProfileImageUrlsContract {

    const val TABLE_NAME = "profile_image_urls"

    object Columns {
        const val ID = "id"
        const val USER_ID = "user_id"
        const val SMALL = "small"
        const val MEDIUM = "medium"
        const val LARGE = "large"
    }
}
