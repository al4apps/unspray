package com.al4gms.unspray.presentation.collections

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.al4gms.unspray.data.ResultCodes.RESULT_CONNECTION_ERROR
import com.al4gms.unspray.data.ResultCodes.RESULT_MISSING_PERMISSIONS_FOR_REQUEST
import com.al4gms.unspray.data.ResultCodes.RESULT_UNKNOWN
import com.al4gms.unspray.data.modelsui.content.Content
import com.al4gms.unspray.data.repositories.CollectionsRepositoryImpl
import com.al4gms.unspray.utils.SingleLiveEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import timber.log.Timber
import java.net.UnknownHostException
import javax.inject.Inject

@HiltViewModel
class CollectionsViewModel @Inject constructor(
    private val collectionsRepository: CollectionsRepositoryImpl,
) : ViewModel() {

    var isLoading: Boolean = false
        private set
    var isLastPage: Boolean = false
        private set
    var hasConnection = true

    var currentPage = 1

    private val _collectionsLiveData = MutableLiveData<List<Content.Collection>>()
    val collectionsLiveData: LiveData<List<Content.Collection>>
        get() = _collectionsLiveData

    private val _isLoadingLiveData = MutableLiveData<Boolean>()
    val isLoadingLiveData: LiveData<Boolean> = _isLoadingLiveData

    private val _resultLiveData = SingleLiveEvent<Int>()
    val resultLiveData: LiveData<Int>
        get() = _resultLiveData

    fun getCollections() {
        viewModelScope.launch {
            try {
                setLoadingStatus(true)
                val list = collectionsRepository.getCollections(currentPage)
                _collectionsLiveData.value = _collectionsLiveData.value?.plus(list) ?: list
                isLastPage = list.size < PAGE_SIZE
            } catch (t: Throwable) {
                _collectionsLiveData.value = _collectionsLiveData.value.orEmpty()
                handleException(t)
            } finally {
                setLoadingStatus(false)
            }
        }
    }

    private fun setLoadingStatus(_isLoading: Boolean) {
        isLoading = _isLoading
        _isLoadingLiveData.value = _isLoading
    }

    private fun handleException(t: Throwable) {
        when (t) {
            is retrofit2.HttpException -> if (t.code() == 403) {
                _resultLiveData.postValue(RESULT_MISSING_PERMISSIONS_FOR_REQUEST)
            } else {
                Timber.d(t)
            }
            is UnknownHostException -> _resultLiveData.value = RESULT_CONNECTION_ERROR
            else -> {
                Timber.d(t)
                _resultLiveData.value = RESULT_UNKNOWN
            }
        }
    }

    companion object {
        private const val PAGE_SIZE = 10
    }
}
