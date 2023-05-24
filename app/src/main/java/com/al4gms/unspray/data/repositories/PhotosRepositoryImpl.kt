package com.al4gms.unspray.data.repositories

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import androidx.room.withTransaction
import com.al4gms.unspray.data.PhotosRemoteMediator
import com.al4gms.unspray.data.SavedValues.PER_PAGE
import com.al4gms.unspray.data.db.PhotosDao
import com.al4gms.unspray.data.db.UnsprayDatabase
import com.al4gms.unspray.data.db.UserDao
import com.al4gms.unspray.data.modelsdb.content.ContentDB
import com.al4gms.unspray.data.modelsdb.content.photo.PhotoDbEntities
import com.al4gms.unspray.data.modelsui.content.Content
import com.al4gms.unspray.data.network.UnsplashApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PhotosRepositoryImpl
@OptIn(ExperimentalPagingApi::class)
@Inject
constructor(
    private val unsplashApi: UnsplashApi,
    private val photosDao: PhotosDao,
    private val userDao: UserDao,
    private val database: UnsprayDatabase,
    private val remoteMediator: PhotosRemoteMediator.Factory,
) : PhotosRepository() {

    @OptIn(ExperimentalPagingApi::class)
    fun getPhotosFlow(query: String?): Flow<PagingData<Content.Photo>> {
        return Pager(
            config = PagingConfig(
                pageSize = PER_PAGE,
                initialLoadSize = PER_PAGE,
            ),
            remoteMediator = remoteMediator.create(query),
            pagingSourceFactory = { photosDao.getPagingSource() },
        )
            .flow
            .map { pagingData ->
                pagingData.map { photo ->
                    photo.toPhotoUi()
                }
            }
    }

    suspend fun searchPhotos(query: String, page: Int): List<Content.Photo> {
        return unsplashApi.searchPhotos(query, page, PER_PAGE).results
    }

    suspend fun getPhotoById(photoId: String): Content.Photo {
        return unsplashApi.getPhoto(photoId)
    }

    suspend fun likeAPhoto(photoId: String): Content.Photo {
        return unsplashApi.likePhoto(photoId).photo
    }

    suspend fun unLikeAPhoto(photoId: String) {
        return unsplashApi.unLikePhoto(photoId)
    }

    suspend fun getUserSPhotos(username: String, page: Int): List<Content.Photo> {
        return unsplashApi.getUserSPhoto(username, page)
    }

    suspend fun getUserSFavorites(username: String, page: Int): List<Content.Photo> {
        return unsplashApi.getUserSLikedPhoto(username, page)
    }

    private suspend fun ContentDB.Photo.toPhotoUi(): Content.Photo {
        return database.withTransaction {
            val userDB = userDao.getUser(this.userId)
            val tagsDB = photosDao.getTags(this.id)

            PhotoDbEntities(
                this,
                userDB,
                this.photoUrls,
                this.exif,
                this.location,
                tagsDB,
            ).toPhoto()
        }
    }
}
