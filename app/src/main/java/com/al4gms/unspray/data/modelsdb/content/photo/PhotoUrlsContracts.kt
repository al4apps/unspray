package com.al4gms.unspray.data.modelsdb.content.photo

object PhotoUrlsContracts {

    const val TABLE_NAME = "photo_urls"

    object Columns {
        const val ID = "id"
        const val PHOTO_ID = "photo_id"
        const val RAW = "raw"
        const val FULL = "full"
        const val REGULAR = "regular"
        const val SMALL = "small"
        const val THUMB = "thumb"
        const val SMALL_S3 = "small_s3"
    }
}
