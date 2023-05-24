package com.al4gms.unspray.di

import com.al4gms.unspray.R
import com.al4gms.unspray.data.modelsui.OnBoardingPage
import com.al4gms.unspray.presentation.start.OnBoardingPageFragment
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class OnBoardingModule {

    @Provides
    @Singleton
    fun providesOBoardingData(): Array<OnBoardingPage> {
        return arrayOf(
            OnBoardingPage(
                R.string.onBoarding_1_page_title,
                R.drawable.ellipse1page,
                R.drawable.cameras1page,
                OnBoardingPageFragment.PAGE_FIRST,
            ),
            OnBoardingPage(
                R.string.onBoarding_2_page_title,
                R.drawable.ellipse2page,
                R.drawable.cameras1page,
                OnBoardingPageFragment.PAGE_SECOND,
            ),
            OnBoardingPage(
                R.string.onBoarding_3_page_title,
                R.drawable.ellipse3page,
                R.drawable.cameras1page,
                OnBoardingPageFragment.PAGE_THIRD,
            ),
        )
    }
}
