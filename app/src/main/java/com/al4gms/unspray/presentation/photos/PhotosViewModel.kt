package com.al4gms.unspray.presentation.photos

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.al4gms.unspray.data.ResultCodes
import com.al4gms.unspray.data.ResultCodes.RESULT_NO_CONNECTION_LOADED_FROM_DB
import com.al4gms.unspray.data.modelsui.content.Content
import com.al4gms.unspray.data.repositories.PhotosRepositoryImpl
import com.al4gms.unspray.utils.SingleLiveEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.launch
import timber.log.Timber
import java.net.UnknownHostException
import javax.inject.Inject

@HiltViewModel
class PhotosViewModel
@Inject constructor(
    private val photosRepository: PhotosRepositoryImpl,
) : ViewModel() {

    var isSearchMode = false
        private set
    private var listBeforeSearchStart: PagingData<Content.Photo> = PagingData.empty()
    var hasConnection = true

    private var fetchingJob: Job? = null
    private var searchingJob: Job? = null
    private var searchTextFlowJob: Job? = null

    private val _photosLiveData = MutableLiveData<PagingData<Content.Photo>>()
    val photosLiveData: LiveData<PagingData<Content.Photo>>
        get() = _photosLiveData

    private val _isLoadingLiveData = MutableLiveData<Boolean>()
    val isLoadingLiveData: LiveData<Boolean> = _isLoadingLiveData

    private val _resultLiveData = SingleLiveEvent<Int>()
    val resultLiveData: LiveData<Int>
        get() = _resultLiveData

    private val _searchModeLiveData = SingleLiveEvent<Boolean>()
    val isSearchModeLiveData: LiveData<Boolean>
        get() = _searchModeLiveData

    init {
        fetchPhotos()
    }

    fun refreshFeed() {
        _photosLiveData.value = PagingData.empty()
        cancelJobs(listOf(fetchingJob, searchTextFlowJob))
        fetchPhotos()
    }

    private fun fetchPhotos() {
        setLoadingState(true)
        fetchingJob?.cancel()
        fetchingJob = viewModelScope.launch {
            photosRepository.getPhotosFlow(null)
                .catch {
                    handleException(it)
                    setLoadingState(false)
                }
                .flowOn(Dispatchers.IO)
                .cachedIn(viewModelScope)
                .collectLatest { pagingData ->
                    _photosLiveData.value = pagingData
                    if (hasConnection.not()) {
                        _resultLiveData.postValue(
                            RESULT_NO_CONNECTION_LOADED_FROM_DB,
                        )
                    }
                    setLoadingState(false)
                }
        }
    }

    private fun searchPhotos(query: String) {
        cancelJobs(listOf(searchingJob, fetchingJob))
        setLoadingState(true)
        searchingJob = viewModelScope.launch {
            photosRepository.getPhotosFlow(query)
                .catch {
                    handleException(it)
                    setLoadingState(false)
                }
                .flowOn(Dispatchers.IO)
                .cachedIn(viewModelScope)
                .collectLatest { pagingData ->
                    _photosLiveData.value = pagingData
                    setLoadingState(false)
                }
        }
    }

    fun searchFlow(textFlow: Flow<String>) {
        searchTextFlowJob?.cancel()
        searchTextFlowJob = textFlow
            .debounce(500)
            .distinctUntilChanged()
            .mapLatest {
                if (it.isNotBlank()) {
                    searchPhotos(it)
                }
            }
            .launchIn(viewModelScope)
    }

    fun switchSearchMode(isSearch: Boolean) {
        _searchModeLiveData.postValue(isSearch)
        isSearchMode = isSearch

        if (isSearch) {
            listBeforeSearchStart = _photosLiveData.value ?: PagingData.empty()
            _photosLiveData.value = PagingData.empty()
        } else {
            fetchPhotos()
            cancelJobs(listOf(searchTextFlowJob, searchingJob))
        }
    }

    fun refreshSearchFeed(query: String) {
        _photosLiveData.value = PagingData.empty()
        cancelJobs(listOf(fetchingJob))
        if (query.isNotEmpty()) searchPhotos(query)
    }

    private fun cancelJobs(jobs: List<Job?>) {
        jobs.forEach { it?.cancel() }
        setLoadingState(false)
    }

    private fun setLoadingState(_isLoading: Boolean) {
        _isLoadingLiveData.postValue(_isLoading)
    }

    private fun handleException(t: Throwable) {
        setLoadingState(false)
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

    override fun onCleared() {
        super.onCleared()
        cancelJobs(listOf(searchTextFlowJob, fetchingJob))
    }
}
