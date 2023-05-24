package com.al4gms.unspray.data.repositories

import net.openid.appauth.AuthorizationRequest
import net.openid.appauth.AuthorizationService
import net.openid.appauth.TokenRequest

abstract class AuthRepository {

    abstract fun getAuthRequest(): AuthorizationRequest
    abstract fun performTokenRequest(
        authService: AuthorizationService,
        tokenRequest: TokenRequest,
        onComplete: (token: String) -> Unit,
        onError: () -> Unit,
    )
    abstract fun saveToken(token: String)
    abstract fun loadAccessTokenFromPrefs(): String?
}
