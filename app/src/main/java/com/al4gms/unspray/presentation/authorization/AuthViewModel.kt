package com.al4gms.unspray.presentation.authorization

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import androidx.browser.customtabs.CustomTabsIntent
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.al4gms.unspray.R
import com.al4gms.unspray.data.repositories.AuthRepositoryImpl
import com.al4gms.unspray.utils.SingleLiveEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import net.openid.appauth.AuthorizationService
import net.openid.appauth.TokenRequest
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepositoryImpl,
    private val context: Context,
) : ViewModel() {

    private val authService: AuthorizationService = AuthorizationService(context)
    private val openAuthPageLiveEvent = SingleLiveEvent<Intent>()
    private val authErrorToastLiveEvent = SingleLiveEvent<Int>()
    private val loadingMutableLiveData = MutableLiveData(false)
    private val authSuccessLiveEvent = SingleLiveEvent<Unit>()

    val openAuthPageLiveData: LiveData<Intent>
        get() = openAuthPageLiveEvent

    val loadingLiveData: LiveData<Boolean>
        get() = loadingMutableLiveData

    val errorToastLiveData: MutableLiveData<Int>
        get() = authErrorToastLiveEvent

    val authSuccessLiveData: LiveData<Unit>
        get() = authSuccessLiveEvent

    fun onAuthCodeReceived(tokenRequest: TokenRequest) {
        loadingMutableLiveData.postValue(true)
        authRepository.performTokenRequest(
            authService = authService,
            tokenRequest = tokenRequest,
            onComplete = { token ->
                loadingMutableLiveData.postValue(false)
                authSuccessLiveEvent.postValue(Unit)
                authRepository.saveToken(token)
            },
            onError = {
                loadingMutableLiveData.postValue(false)
                authErrorToastLiveEvent.postValue(R.string.auth_canceled)
            },
        )
    }

    fun openLoginPage() {
        val customTabsIntent = CustomTabsIntent.Builder()
            .setShowTitle(true)
            .setUrlBarHidingEnabled(true)
            .build()

        try {
            val openAuthPageIntent = authService.getAuthorizationRequestIntent(
                authRepository.getAuthRequest(),
                customTabsIntent,
            )
            openAuthPageLiveEvent.postValue(openAuthPageIntent)
        } catch (t: Throwable) {
            if (t is ActivityNotFoundException) {
                errorToastLiveData.value = R.string.auth_canceled
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        authService.dispose()
    }
}
