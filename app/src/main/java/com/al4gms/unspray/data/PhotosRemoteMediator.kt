package com.al4gms.unspray.data

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.al4gms.unspray.data.db.PhotosDao
import com.al4gms.unspray.data.db.UnsprayDatabase
import com.al4gms.unspray.data.db.UserDao
import com.al4gms.unspray.data.modelsdb.content.ContentDB
import com.al4gms.unspray.data.modelsdb.content.photo.PhotoDbEntities
import com.al4gms.unspray.data.network.UnsplashApi
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject

@ExperimentalPagingApi
class PhotosRemoteMediator @AssistedInject constructor(
    private val unsplashApi: UnsplashApi,
    private val database: UnsprayDatabase,
    private val userDao: UserDao,
    private val photosDao: PhotosDao,
    @Assisted private val query: String?,
) : RemoteMediator<Int, ContentDB.Photo>() {

    private var pageIndex = DEFAULT_PAGE_NUMBER

    override suspend fun initialize(): InitializeAction {
        return InitializeAction.LAUNCH_INITIAL_REFRESH
    }

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, ContentDB.Photo>,
    ): MediatorResult {
        pageIndex = getPageIndex(loadType)
            ?: return MediatorResult.Success(endOfPaginationReached = true)

        val limit = state.config.pageSize

        return try {
            val photoDbEntities = if (query == null) {
                fetchPhotos(pageIndex, limit)
            } else {
                searchPhotos(pageIndex, limit, query)
            }
            val photos = photoDbEntities.map { it.photoDB }

            if (loadType == LoadType.REFRESH) {
                refresh(photoDbEntities)
            } else {
                savePhotos(photoDbEntities)
            }

            MediatorResult.Success(
                endOfPaginationReached = photos.size < limit,
            )
        } catch (t: Throwable) {
            MediatorResult.Error(t)
        }
    }

    private fun getPageIndex(loadType: LoadType): Int? {
        pageIndex = when (loadType) {
            LoadType.REFRESH -> DEFAULT_PAGE_NUMBER
            LoadType.PREPEND -> return null
            LoadType.APPEND -> ++pageIndex
        }
        return pageIndex
    }

    private suspend fun fetchPhotos(pageIndex: Int, pageSize: Int): List<PhotoDbEntities> {
        return unsplashApi.getPhotos(pageIndex, pageSize).map { it.toDbEntities() }
    }

    private suspend fun searchPhotos(pageIndex: Int, pageSize: Int, query: String): List<PhotoDbEntities> {
        return unsplashApi.searchPhotos(query, pageIndex, pageSize)
            .results
            .map { it.toDbEntities() }
    }

    private suspend fun savePhotos(dbEntities: List<PhotoDbEntities>) {
        database.withTransaction {
            userDao.saveUsers(dbEntities.map { it.userDB })
            photosDao.savePhotoEntities(dbEntities)
        }
    }

    private suspend fun refresh(dbEntities: List<PhotoDbEntities>) {
        photosDao.refresh(dbEntities)
        var users = dbEntities.map { it.userDB }
        SavedValues.currentUser?.let { users = users.plus(it.toUserDb()) }
        userDao.refresh(users)
    }

    @AssistedFactory
    interface Factory {
        fun create(query: String?): PhotosRemoteMediator
    }

    companion object {
        private const val DEFAULT_PAGE_NUMBER = 1
    }
}
