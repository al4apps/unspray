package com.al4gms.unspray.data.repositories

import android.content.Context
import com.al4gms.unspray.data.modelsui.OnBoardingPage
import javax.inject.Inject

class StartingRepositoryImpl @Inject constructor(
    private val onBoardingPages: Array<OnBoardingPage>,
    private val context: Context,
) : StartingRepository() {

    private val firstLaunchSharedPrefs by lazy {
        context.getSharedPreferences(FIRST_LAUNCH_SHARED_PREFS_NAME, Context.MODE_PRIVATE)
    }

    override fun getOnBoardingPages(): Array<OnBoardingPage> {
        return onBoardingPages
    }

    override fun isFirstLaunch(): Boolean {
        return firstLaunchSharedPrefs.getBoolean(FIRST_LAUNCH_KEY, true)
    }

    override fun editFirstLaunchFlag() {
        firstLaunchSharedPrefs.edit()
            .putBoolean(FIRST_LAUNCH_KEY, false)
            .apply()
    }

    companion object {
        private const val FIRST_LAUNCH_SHARED_PREFS_NAME = "first_launch_shared_prefs"
        private const val FIRST_LAUNCH_KEY = "first_launch"
    }
}
