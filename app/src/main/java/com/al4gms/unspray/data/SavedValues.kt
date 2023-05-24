package com.al4gms.unspray.data

import androidx.lifecycle.MutableLiveData
import com.al4gms.unspray.data.modelsui.user.User
import com.al4gms.unspray.utils.SingleLiveEvent
import kotlinx.coroutines.flow.flow
import timber.log.Timber

object SavedValues {

    const val PER_PAGE = 20
    const val FIRST_PAGE_DEFAULT = 1
    var currentUser: User? = null
        private set

    var isAppReadyToStart: Boolean = false

    var requestCount = 0
        private set

    val connectionLiveData = SingleLiveEvent<Boolean>()

    fun setUser(user: User) {
        currentUser = user
    }

    fun deleteUser() {
        currentUser = null
    }

    fun incrementRequestCount() {
        requestCount++
        Timber.d("requestCount = $requestCount")
    }
    fun resetRequestCount() {
        requestCount = 0
        Timber.d("requestCount = $requestCount")
    }
    fun setRequestCount(count: Int) {
        requestCount = count
    }

    fun getPhotoShareLink(id: String): String {
        val link = "https://unsplash.com/photos/"
        return link + id
    }

    fun getCollectionsShareLink(id: String): String {
        val link = "https://unsplash.com/collections/"
        return link + id
    }
}
