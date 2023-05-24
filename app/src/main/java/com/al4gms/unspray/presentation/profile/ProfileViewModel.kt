package com.al4gms.unspray.presentation.profile

import androidx.browser.customtabs.CustomTabsIntent
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.al4gms.unspray.data.RequestState
import com.al4gms.unspray.data.ResultCodes
import com.al4gms.unspray.data.ResultCodes.RESULT_CONNECTION_ERROR
import com.al4gms.unspray.data.ResultCodes.RESULT_MISSING_PERMISSIONS_FOR_REQUEST
import com.al4gms.unspray.data.ResultCodes.RESULT_NO_USER_INFO
import com.al4gms.unspray.data.SavedValues
import com.al4gms.unspray.data.modelsui.user.User
import com.al4gms.unspray.data.repositories.AuthRepositoryImpl
import com.al4gms.unspray.data.repositories.ProfileRepositoryImpl
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import timber.log.Timber
import java.net.UnknownHostException
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val profileRepository: ProfileRepositoryImpl,
    private val authRepository: AuthRepositoryImpl,
) : ViewModel() {

    var hasConnection = true

    private val _userInfoLiveData = MutableLiveData<User>()
    val userInfoLiveData: LiveData<User> = _userInfoLiveData

    private val _isLoadingLiveData = MutableLiveData<Boolean>()
    val isLoadingLiveData: LiveData<Boolean> = _isLoadingLiveData

    private val _resultLiveData = MutableLiveData<Int>()
    val resultLiveData: LiveData<Int>
        get() = _resultLiveData

    fun getUserInfoNetwork() {
        viewModelScope.launch {
            try {
                setLoadingState(true)
                val user = profileRepository.getUserInfo()
                if (_userInfoLiveData.value != user) _userInfoLiveData.value = user
                cacheNewlyInfoCurrentUser(user)
            } catch (t: Throwable) {
                handleException(t)
            } finally {
                setLoadingState(false)
                if (_userInfoLiveData.value == null) getCachedUserInfo()
            }
        }
    }

    private fun cacheNewlyInfoCurrentUser(user: User) {
        SavedValues.setUser(user)
        viewModelScope.launch {
            try {
                profileRepository.cacheCurrentUser(user)
            } catch (t: Throwable) {
                handleException(t)
            }
        }
    }

    private fun getCachedUserInfo() {
        viewModelScope.launch {
            try {
                setLoadingState(true)
                val user = SavedValues.currentUser ?: profileRepository.getCurrentUserDB()
                user?.let {
                    if (_userInfoLiveData.value == null) _userInfoLiveData.value = it
                }
            } catch (t: Throwable) {
                handleException(t)
            } finally {
                setLoadingState(false)
                if (_userInfoLiveData.value == null) {
                    _resultLiveData.value =
                        RESULT_NO_USER_INFO
                }
            }
        }
    }

    fun cacheCurrentUser() {
        if (SavedValues.currentUser != null) return
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val cachedUser = profileRepository.getCurrentUserDB()
                if (cachedUser == null) {
                    val user = profileRepository.getUserInfo()
                    SavedValues.setUser(user)
                    profileRepository.cacheCurrentUser(user)
                } else {
                    SavedValues.setUser(cachedUser)
                }
            } catch (t: Throwable) {
                handleException(t)
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            try {
                authRepository.removeSharedPrefs()
                profileRepository.clearDatabase()
                profileRepository.removeSharedPrefs()
                SavedValues.deleteUser()
                _resultLiveData.value = ProfileFragment.RESULT_LOGOUT_SUCCESS
            } catch (t: Throwable) {
                handleException(t)
            }
        }
    }

    private fun setLoadingState(isLoading: Boolean) {
        if (isLoading) {
            if (_userInfoLiveData.value == null) _isLoadingLiveData.value = true
        } else {
            _isLoadingLiveData.value = false
        }
    }

    val counterLiveData = MutableLiveData<Int>()
    fun getRequestCount() {
        var minute = 0
        var count = 0
        viewModelScope.launch {
            val state = profileRepository.getRequestState()
            val hh = Clock.System.now()
                .toLocalDateTime(TimeZone.currentSystemDefault()).hour

            SavedValues.setRequestCount(
                if (System.currentTimeMillis() - state.millis <= 3600000L && state.hour == hh) {
                    state.count
                } else {
                    0
                },
            )
            counterLiveData.value = SavedValues.requestCount

            while (count <= 50) {
                val now: Instant = Clock.System.now()
                val newMinute = now.toLocalDateTime(TimeZone.currentSystemDefault()).minute
                val millis = System.currentTimeMillis()
                val hour = now.toLocalDateTime(TimeZone.currentSystemDefault()).hour
                val requestCount = SavedValues.requestCount

                if (requestCount != count) {
                    counterLiveData.value = requestCount
                    profileRepository.saveRequestState(RequestState(millis, hour, requestCount))
                }
                if (newMinute < minute) SavedValues.resetRequestCount()
                count = requestCount
                minute = newMinute
                delay(1000)
            }
        }
    }

    private fun handleException(t: Throwable) {
        when (t) {
            is retrofit2.HttpException -> {
                if (t.code() == 403) {
                    _resultLiveData.postValue(
                        RESULT_MISSING_PERMISSIONS_FOR_REQUEST,
                    )
                }
                Timber.d("Throwable: ${t.code()}")
            }
            is UnknownHostException -> _resultLiveData.postValue(RESULT_CONNECTION_ERROR)
            else -> {
                Timber.d(t)
                _resultLiveData.postValue(ResultCodes.RESULT_UNKNOWN)
            }
        }
    }
}
