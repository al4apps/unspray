package com.al4gms.unspray.data.modelsdb.content

import com.al4gms.unspray.data.modelsdb.user.UserDB

data class CollectionDbEntities(
    val collection: ContentDB.Collection,
    val user: UserDB,
    val coverPhoto: ContentDB.Photo,
    val tags: List<TagDB>?,
)
