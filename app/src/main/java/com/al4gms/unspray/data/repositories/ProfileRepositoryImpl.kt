package com.al4gms.unspray.data.repositories

import android.content.Context
import com.al4gms.unspray.data.RequestState
import com.al4gms.unspray.data.db.UnsprayDatabase
import com.al4gms.unspray.data.db.UserDao
import com.al4gms.unspray.data.modelsdb.user.UserDB
import com.al4gms.unspray.data.modelsui.user.ProfileImageUrls
import com.al4gms.unspray.data.modelsui.user.User
import com.al4gms.unspray.data.network.UnsplashApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject

class ProfileRepositoryImpl @Inject constructor(
    private val unsplashApi: UnsplashApi,
    private val database: UnsprayDatabase,
    private val userDao: UserDao,
    private val context: Context,
) : ProfileRepository() {

    private val currentUserSharedPrefs by lazy {
        context.getSharedPreferences(CURRENT_USER_SHARED_PREFS, Context.MODE_PRIVATE)
    }

    private val requestCountSharedPrefs by lazy {
        context.getSharedPreferences(REQUEST_COUNTER, Context.MODE_PRIVATE)
    }

    suspend fun getUserInfo(): User {
        return unsplashApi.getCurrentUserInfo()
    }

    suspend fun cacheCurrentUser(user: User) = withContext(Dispatchers.IO) {
        currentUserSharedPrefs.edit()
            .putString(USER_ID_KEY, user.id)
            .apply()

        userDao.saveUsers(listOf(user.toUserDb()))
    }

    suspend fun getCurrentUserDB(): User? = withContext(Dispatchers.IO) {
        val id = currentUserSharedPrefs.getString(USER_ID_KEY, null)
        val user = id?.let { userDao.getUser(it) }?.toUserUi()
        Timber.d("me from DB = $user")
        return@withContext user
    }

    suspend fun clearDatabase() = withContext(Dispatchers.IO) {
        database.clearAllTables()
    }

    suspend fun removeSharedPrefs() = withContext(Dispatchers.IO) {
        currentUserSharedPrefs.edit().remove(USER_ID_KEY).commit()
    }

    private suspend fun UserDB.toUserUi(): User = withContext(Dispatchers.IO) {
        return@withContext User(
            id = id,
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
            profileImage = ProfileImageUrls(
                small = profileImageSmall,
                medium = profileImageMedium,
                large = profileImageLarge,
            ),
        )
    }

    suspend fun saveRequestState(requestState: RequestState) {
        withContext(Dispatchers.IO) {
            requestCountSharedPrefs.edit()
                .putLong(DATE_KEY, requestState.millis)
                .putInt(HOUR_KEY, requestState.hour)
                .putInt(COUNT_KEY, requestState.count)
                .apply()
        }
    }

    suspend fun getRequestState(): RequestState =
        withContext(Dispatchers.IO) {
            val millis = requestCountSharedPrefs.getLong(DATE_KEY, 0)
            val hour = requestCountSharedPrefs.getInt(HOUR_KEY, 0)
            val count = requestCountSharedPrefs.getInt(COUNT_KEY, 0)
            RequestState(millis, hour, count)
        }

    companion object {
        private const val CURRENT_USER_SHARED_PREFS = "current_user_shared_prefs"
        private const val USER_ID_KEY = "user_id_key"

        private const val REQUEST_COUNTER = "request_counter"
        private const val DATE_KEY = "date"
        private const val HOUR_KEY = "hour"
        private const val COUNT_KEY = "count"
    }
}
