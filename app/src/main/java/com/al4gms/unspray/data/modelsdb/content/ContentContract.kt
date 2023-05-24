package com.al4gms.unspray.data.modelsdb.content

object ContentContract {
    const val COLLECTION_TABLE_NAME = "collection"
    const val PHOTO_TABLE_NAME = "photo"

    object CollectionColumn {
        const val ID = "id"
        const val TITLE = "title"
        const val DESCRIPTION = "description"
        const val TOTAL_PHOTOS = "total_photos"
        const val PRIVATE = "private"
        const val COVER_PHOTO_ID = "cover_photo_id"
        const val USER_ID = "user_id"
        const val TAGS = "tags"
    }

    object PhotosColumn {
        const val ID = "id"
        const val CREATED_AT = "created_at"
        const val WIDTH = "width"
        const val HEIGHT = "height"
        const val BLUR_HASH = "blur_hash"
        const val DOWNLOADS = "downloads"
        const val LIKES = "likes"
        const val LIKED_BY_USER = "liked_by_user"
        const val USER_ID = "user_id"
        const val DESCRIPTION = "description"
        const val DOWNLOAD_LINK = "download_link"
        const val PHOTO_URLS = "photo_urls"
        const val EXIF = "exif"
        const val LOCATION = "location"
    }
}
