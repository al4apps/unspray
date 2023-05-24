package com.al4gms.unspray.data.repositories

import android.content.Context
import android.net.Uri
import com.al4gms.unspray.data.AccessToken
import com.al4gms.unspray.data.network.AuthConfig
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import net.openid.appauth.AuthorizationRequest
import net.openid.appauth.AuthorizationService
import net.openid.appauth.AuthorizationServiceConfiguration
import net.openid.appauth.ClientAuthentication
import net.openid.appauth.ClientSecretPost
import net.openid.appauth.TokenRequest
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val context: Context,
) : AuthRepository() {

    private val tokenSharedPrefs by lazy {
        context.getSharedPreferences(AUTH_SHARED_PREFS_NAME, Context.MODE_PRIVATE)
    }

    private val scope = CoroutineScope(SupervisorJob())
    private val authConfig = AuthConfig

    override fun getAuthRequest(): AuthorizationRequest {
        val serviceConfiguration = AuthorizationServiceConfiguration(
            Uri.parse(authConfig.AUTH_URI),
            Uri.parse(authConfig.TOKEN_URI),
        )

        val redirectUri = Uri.parse(AuthConfig.CALLBACK_URL)

        return AuthorizationRequest.Builder(
            serviceConfiguration,
            authConfig.CLIENT_ID,
            authConfig.RESPONSE_TYPE,
            redirectUri,
        )
            .setScope(authConfig.SCOPE)
            .build()
    }

    override fun performTokenRequest(
        authService: AuthorizationService,
        tokenRequest: TokenRequest,
        onComplete: (token: String) -> Unit,
        onError: () -> Unit,
    ) {
        authService.performTokenRequest(tokenRequest, getClientAuthentication()) { response, _ ->
            when {
                response != null -> {
                    val accessToken = response.accessToken.orEmpty()
                    onComplete(accessToken)
                }
                else -> onError()
            }
        }
    }

    private fun getClientAuthentication(): ClientAuthentication {
        return ClientSecretPost(AuthConfig.CLIENT_SECRET)
    }

    override fun saveToken(token: String) {
        scope.launch(Dispatchers.IO) {
            tokenSharedPrefs.edit()
                .putString(AUTH_TOKEN_KEY, token)
                .apply()
            AccessToken.setToken(token)
        }
    }

    suspend fun removeSharedPrefs() = withContext(Dispatchers.IO) {
        tokenSharedPrefs.edit().remove(AUTH_TOKEN_KEY).commit()
    }

    override fun loadAccessTokenFromPrefs(): String? {
        return tokenSharedPrefs.getString(AUTH_TOKEN_KEY, null)
    }

    companion object {
        private const val AUTH_SHARED_PREFS_NAME = "auth_token_shared_prefs"
        private const val AUTH_TOKEN_KEY = "auth_token"
    }
}
