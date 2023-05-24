package com.al4gms.unspray.presentation.photos

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.al4gms.unspray.data.ResultCodes
import com.al4gms.unspray.data.modelsui.content.Content
import com.al4gms.unspray.data.repositories.PhotosRepositoryImpl
import com.al4gms.unspray.service.DownloaderImpl
import com.al4gms.unspray.utils.SingleLiveEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import timber.log.Timber
import java.net.UnknownHostException
import javax.inject.Inject

@HiltViewModel
class PhotoViewModel
@Inject constructor(
    private val photosRepository: PhotosRepositoryImpl,
    private val downloader: DownloaderImpl,
) : ViewModel() {

    var currentPhoto: Content.Photo? = null
    private var isLiked = false
    var hasConnection = true

    private val _photoDetailInfoLiveData = SingleLiveEvent<Content.Photo>()
    val photoDetailInfoLiveData: LiveData<Content.Photo>
        get() = _photoDetailInfoLiveData

    private val _photoInfoLiveData = MutableLiveData<Content.Photo>()
    val photoInfoLiveData: LiveData<Content.Photo> = _photoInfoLiveData

    private val _isLikingLiveData = MutableLiveData<Boolean>()
    val isLikingLiveData: LiveData<Boolean> = _isLikingLiveData

    private val _isLoadingLiveData = MutableLiveData<Boolean>()
    val isLoadingLiveData: LiveData<Boolean> = _isLoadingLiveData

    private val _resultLiveData = SingleLiveEvent<Int>()
    val resultLiveData: LiveData<Int>
        get() = _resultLiveData

    private val _downloadIdLiveData = SingleLiveEvent<Long>()
    val downloadIdLiveData: LiveData<Long>
        get() = _downloadIdLiveData

    fun getPhoto(photoId: String) {
        viewModelScope.launch {
            try {
                val photo = photosRepository.getPhotoById(photoId)
                currentPhoto = photo
                isLiked = photo.likedByUser
                _photoDetailInfoLiveData.value = photo
                _photoInfoLiveData.value = photo
            } catch (t: Throwable) {
                handleException(t)
            }
        }
    }

    fun downloadThisImage() {
        val downloadId = downloader.downloadFileWithDM(
            _photoDetailInfoLiveData.value?.urls?.raw ?: return,
        )
        _downloadIdLiveData.value = downloadId
    }

    fun permissionsDenied() {
        _resultLiveData.postValue(ResultCodes.RESULT_PERMISSIONS_DENIED)
    }

    fun likeOrUnLikePhoto(photoId: String) {
        Timber.d("isLiked = $isLiked")
        if (isLiked.not()) {
            likeAPhoto(photoId)
        } else {
            unLikeAPhoto(photoId)
        }
    }

    private fun likeAPhoto(photoId: String) {
        viewModelScope.launch {
            try {
                val photo = photosRepository.likeAPhoto(photoId)
                _photoInfoLiveData.value = photo
                isLiked = photo.likedByUser
                _isLikingLiveData.value = true
                delay(LIKING_PROGRESS_INTERVAL)
                _isLikingLiveData.value = false
            } catch (t: Throwable) {
                handleException(t)
            }
        }
    }

    private fun unLikeAPhoto(photoId: String) {
        viewModelScope.launch {
            try {
                photosRepository.unLikeAPhoto(photoId)
                val photo = photosRepository.getPhotoById(photoId)
                _photoInfoLiveData.postValue(photo)
                isLiked = photo.likedByUser
            } catch (t: Throwable) {
                handleException(t)
            }
        }
    }

    private fun handleException(t: Throwable) {
        when (t) {
            is retrofit2.HttpException -> if (t.code() == 403) {
                _resultLiveData.postValue(ResultCodes.RESULT_MISSING_PERMISSIONS_FOR_REQUEST)
            } else {
                Timber.d(t)
            }
            is UnknownHostException -> _resultLiveData.value = ResultCodes.RESULT_CONNECTION_ERROR
            else -> {
                Timber.d(t)
                _resultLiveData.value = ResultCodes.RESULT_UNKNOWN
            }
        }
    }
    companion object {
        private const val LIKING_PROGRESS_INTERVAL = 1000L
    }
}
