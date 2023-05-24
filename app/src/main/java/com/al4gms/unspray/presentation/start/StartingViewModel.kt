package com.al4gms.unspray.presentation.start

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.al4gms.unspray.data.modelsui.OnBoardingPage
import com.al4gms.unspray.data.repositories.AuthRepositoryImpl
import com.al4gms.unspray.data.repositories.StartingRepositoryImpl
import com.al4gms.unspray.utils.SingleLiveEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class StartingViewModel @Inject constructor(
    private val startingRepository: StartingRepositoryImpl,
    private val authRepository: AuthRepositoryImpl,
) : ViewModel() {

    var currentPosition = 0

    private val _pagesLiveData = SingleLiveEvent<Array<OnBoardingPage>>()
    val pagesLiveData: LiveData<Array<OnBoardingPage>>
        get() = _pagesLiveData

    private val _isFirstLaunchLiveData = SingleLiveEvent<Boolean>()
    val isFirstLaunchLiveData: LiveData<Boolean>
        get() = _isFirstLaunchLiveData

    private val _hasTokenLiveData = SingleLiveEvent<Boolean>()
    val hasTokenLiveData: LiveData<Boolean>
        get() = _hasTokenLiveData

    fun getOnBoardingPages() {
        _pagesLiveData.value = startingRepository.getOnBoardingPages()
    }

    fun checkFirstLaunchFlag() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val isFirstStart = startingRepository.isFirstLaunch()
                _isFirstLaunchLiveData.postValue(isFirstStart)
            } catch (t: Throwable) {
                Timber.d(t)
            }
        }
    }

    fun editFirstLaunchFlag() {
        try {
            startingRepository.editFirstLaunchFlag()
        } catch (t: Throwable) {
            Timber.d(t)
        }
    }

    fun getTokenFromSharedPrefs() {
        try {
            val token = authRepository.loadAccessTokenFromPrefs()
            if (token != null) {
                _hasTokenLiveData.postValue(true)
                authRepository.saveToken(token)
            } else {
                _hasTokenLiveData.postValue(false)
            }
        } catch (t: Throwable) {
            Timber.d(t)
        }
    }
}
