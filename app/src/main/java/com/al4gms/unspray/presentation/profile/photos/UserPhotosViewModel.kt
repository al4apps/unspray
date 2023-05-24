package com.al4gms.unspray.presentation.profile.photos

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.al4gms.unspray.data.ResultCodes.RESULT_CONNECTION_ERROR
import com.al4gms.unspray.data.ResultCodes.RESULT_MISSING_PERMISSIONS_FOR_REQUEST
import com.al4gms.unspray.data.ResultCodes.RESULT_NO_PHOTOS
import com.al4gms.unspray.data.ResultCodes.RESULT_UNKNOWN
import com.al4gms.unspray.data.SavedValues
import com.al4gms.unspray.data.modelsui.content.Content
import com.al4gms.unspray.data.modelsui.user.User
import com.al4gms.unspray.data.repositories.PhotosRepositoryImpl
import com.al4gms.unspray.data.repositories.ProfileRepositoryImpl
import com.al4gms.unspray.utils.SingleLiveEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import timber.log.Timber
import java.net.UnknownHostException
import javax.inject.Inject

@HiltViewModel
class UserPhotosViewModel
@Inject constructor(
    private val photosRepository: PhotosRepositoryImpl,
    private val profileRepository: ProfileRepositoryImpl,
) : ViewModel() {

    var isLoading: Boolean = false
        private set
    var isLastPage: Boolean = false
        private set

    var currentPage = 1
    var currentList: List<Content> = emptyList()

    private val _photosLiveData = MutableLiveData<List<Content.Photo>>()
    val photosLiveData: LiveData<List<Content.Photo>>
        get() = _photosLiveData

    private val _isLoadingLiveData = MutableLiveData<Boolean>()
    val isLoadingLiveData: LiveData<Boolean>
        get() = _isLoadingLiveData

    private val _resultLiveData = SingleLiveEvent<Int>()
    val resultLiveData: LiveData<Int>
        get() = _resultLiveData

    fun getUserSContent(type: Int) {
        viewModelScope.launch {
            try {
                setLoadingState(true)
                val user = getUserInfo()
                if (type == TYPE_LIKES) getFavorites(user)
                if (type == TYPE_PHOTOS) getPhotos(user)
            } catch (t: Throwable) {
                _photosLiveData.value = _photosLiveData.value.orEmpty()
                handleException(t)
            } finally {
                setLoadingState(false)
            }
        }
    }

    private suspend fun getFavorites(user: User) {
        if (user.totalLikes == 0) {
            _resultLiveData.value = RESULT_NO_PHOTOS
            return
        }
        val list = photosRepository.getUserSFavorites(user.username, currentPage)

        if (_photosLiveData.value != list) {
            _photosLiveData.value = _photosLiveData.value?.plus(list) ?: list
        }
        isLastPage = currentPage * PAGE_SIZE >= user.totalLikes
    }

    private suspend fun getPhotos(user: User) {
        if (user.totalPhotos == 0) {
            _resultLiveData.value = RESULT_NO_PHOTOS
            return
        }
        val list = photosRepository.getUserSPhotos(user.username, currentPage)

        if (_photosLiveData.value != list) {
            _photosLiveData.value = _photosLiveData.value?.plus(list) ?: list
        }
        isLastPage = currentPage * PAGE_SIZE >= user.totalPhotos
        if (isLastPage.not()) currentPage++
    }

    fun refreshList(type: Int) {
        viewModelScope.launch {
            _photosLiveData.value = emptyList()
            currentPage = 1
            getUserSContent(type)
        }
    }

    private suspend fun getUserInfo(): User {
        val user = SavedValues.currentUser ?: profileRepository.getCurrentUserDB()
            ?: profileRepository.getUserInfo()
        SavedValues.setUser(user)
        return user
    }

    private fun setLoadingState(_isLoading: Boolean) {
        isLoading = _isLoading
        _isLoadingLiveData.value = _isLoading
    }

    private fun handleException(t: Throwable) {
        setLoadingState(false)
        when (t) {
            is retrofit2.HttpException -> if (t.code() == 403) RESULT_MISSING_PERMISSIONS_FOR_REQUEST
            is UnknownHostException -> _resultLiveData.postValue(RESULT_CONNECTION_ERROR)
            else -> {
                Timber.d(t)
                _resultLiveData.value = RESULT_UNKNOWN
            }
        }
    }

    companion object {
        private const val PAGE_SIZE = 10

        const val TYPE_PHOTOS = 111
        const val TYPE_LIKES = 222
    }
}
