package com.al4gms.unspray.presentation.profile.collections

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.al4gms.unspray.data.ResultCodes
import com.al4gms.unspray.data.ResultCodes.RESULT_NO_COLLECTIONS
import com.al4gms.unspray.data.SavedValues
import com.al4gms.unspray.data.modelsui.content.Content
import com.al4gms.unspray.data.modelsui.user.User
import com.al4gms.unspray.data.repositories.CollectionsRepositoryImpl
import com.al4gms.unspray.data.repositories.ProfileRepositoryImpl
import com.al4gms.unspray.utils.SingleLiveEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import timber.log.Timber
import java.net.UnknownHostException
import javax.inject.Inject

@HiltViewModel
class UserCollectionsViewModel @Inject constructor(
    private val collectionsRepository: CollectionsRepositoryImpl,
    private val profileRepository: ProfileRepositoryImpl,
) : ViewModel() {

    var isLoading: Boolean = false
        private set
    var isLastPage: Boolean = false
        private set

    var currentList: List<Content.Collection> = emptyList()
    private var currentPage = 1
    lateinit var collectionId: String

    private val _collectionsLiveData = MutableLiveData<List<Content.Collection>>()
    val collectionsLiveData: LiveData<List<Content.Collection>>
        get() = _collectionsLiveData

    private val _collectionInfo = MutableLiveData<Content.Collection>()
    val collectionInfo: LiveData<Content.Collection>
        get() = _collectionInfo

    private val _photosLiveData = MutableLiveData<List<Content.Photo>>()
    val photosLiveData: LiveData<List<Content.Photo>>
        get() = _photosLiveData

    private val _isLoadingLiveData = MutableLiveData<Boolean>()
    val isLoadingLiveData: LiveData<Boolean> = _isLoadingLiveData

    private val _resultLiveData = SingleLiveEvent<Int>()
    val resultLiveData: LiveData<Int>
        get() = _resultLiveData

    fun getUserCollections() {
        viewModelScope.launch {
            try {
                setLoadingStatus(true)
                val user = getUserInfo()
                if (user.totalCollections == 0) {
                    _resultLiveData.value = RESULT_NO_COLLECTIONS
                    return@launch
                }
                val list = collectionsRepository.getUserSCollections(user.username, currentPage)
                if (_collectionsLiveData.value != list) {
                    _collectionsLiveData.value = _collectionsLiveData.value?.plus(list) ?: list
                }
                isLastPage = currentPage * PAGE_SIZE >= user.totalCollections
                if (isLastPage.not()) currentPage++
            } catch (t: Throwable) {
                _collectionsLiveData.value = _collectionsLiveData.value.orEmpty()
                handleException(t)
            } finally {
                setLoadingStatus(false)
            }
        }
    }

    fun refreshList() {
        viewModelScope.launch {
            _collectionsLiveData.value = emptyList()
            currentPage = 1
            getUserCollections()
        }
    }

    fun refreshCollection() {
        viewModelScope.launch {
            _photosLiveData.value = emptyList()
            currentPage = 1
            getCollection()
            getPhotosInCollection()
        }
    }

    fun getCollection() {
        viewModelScope.launch {
            try {
                val collection = collectionsRepository.getCollectionInfo(collectionId)
                _collectionInfo.value = collection
                isLastPage = currentPage * PAGE_SIZE >= collection.totalPhotos
            } catch (t: Throwable) {
                handleException(t)
            }
        }
    }

    fun getPhotosInCollection() {
        viewModelScope.launch {
            try {
                setLoadingStatus(true)
                val list = collectionsRepository.getCollectionsPhotos(collectionId, currentPage)
                if (_photosLiveData.value != list) {
                    _photosLiveData.value = _photosLiveData.value?.plus(list) ?: list
                }
                if (isLastPage.not()) currentPage++
            } catch (t: Throwable) {
                _photosLiveData.value = _photosLiveData.value.orEmpty()
                handleException(t)
            } finally {
                setLoadingStatus(false)
            }
        }
    }

    private suspend fun getUserInfo(): User {
        val user = SavedValues.currentUser ?: profileRepository.getCurrentUserDB()
            ?: profileRepository.getUserInfo()
        SavedValues.setUser(user)
        return user
    }

    private fun setLoadingStatus(_isLoading: Boolean) {
        isLoading = _isLoading
        _isLoadingLiveData.value = _isLoading
    }

    private fun handleException(t: Throwable) {
        when (t) {
            is retrofit2.HttpException -> if (t.code() == 403) ResultCodes.RESULT_MISSING_PERMISSIONS_FOR_REQUEST
            is UnknownHostException -> _resultLiveData.postValue(ResultCodes.RESULT_CONNECTION_ERROR)
            else -> {
                Timber.d(t)
                _resultLiveData.value = ResultCodes.RESULT_UNKNOWN
            }
        }
    }

    companion object {
        private const val PAGE_SIZE = 10
    }
}
