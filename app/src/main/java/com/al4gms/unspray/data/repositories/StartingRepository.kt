package com.al4gms.unspray.data.repositories

import com.al4gms.unspray.data.modelsui.OnBoardingPage

abstract class StartingRepository {

    abstract fun getOnBoardingPages(): Array<OnBoardingPage>
    abstract fun isFirstLaunch(): Boolean
    abstract fun editFirstLaunchFlag()
}
