package com.al4gms.unspray.data.repositories

import com.al4gms.unspray.data.modelsui.content.Content
import com.al4gms.unspray.data.network.UnsplashApi
import javax.inject.Inject

class CollectionsRepositoryImpl @Inject constructor(
    private val unsplashApi: UnsplashApi,
) : CollectionsRepository() {

    suspend fun getCollections(page: Int): List<Content.Collection> {
        return unsplashApi.getCollections(page)
    }

    suspend fun getCollectionInfo(id: String): Content.Collection {
        return unsplashApi.getCollectionInfo(id)
    }

    suspend fun getCollectionsPhotos(collectionId: String, page: Int): List<Content.Photo> {
        return unsplashApi.getCollectionsPhotos(collectionId, page)
    }

    suspend fun getUserSCollections(username: String, page: Int): List<Content.Collection> {
        return unsplashApi.getUserSCollections(username, page)
    }
}
