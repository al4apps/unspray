package com.al4gms.unspray.data.network

import net.openid.appauth.ResponseTypeValues

object AuthConfig {

    const val AUTH_URI = "https://unsplash.com/oauth/authorize"
    const val TOKEN_URI = "https://unsplash.com/oauth/token"
    const val RESPONSE_TYPE = ResponseTypeValues.CODE
    const val SCOPE = "public read_user read_photos write_likes read_collections"

    const val CLIENT_ID = "mCHpVMMzkVrmmQiNcLNGpM3DUt3Nr9o5QIJ3zIqXPuI"
    const val CLIENT_SECRET = "epzGErkKs2WC24URFU4lLoaGx7zRXK2zi5iwLbMOINI"
    const val CALLBACK_URL = "unspray://com.al4gms.unspray/callback"
}
