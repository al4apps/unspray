package com.al4gms.unspray.data

object AccessToken {
    lateinit var accessToken: String

    fun setToken(token: String) {
        accessToken = token
    }
}
