package com.al4gms.unspray.data.modelsui

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes

data class OnBoardingPage(
    @StringRes val title: Int,
    @DrawableRes val ellipseDrawableRes: Int,
    @DrawableRes val camerasDrawableRes: Int,
    val pageNumber: Int,
)
