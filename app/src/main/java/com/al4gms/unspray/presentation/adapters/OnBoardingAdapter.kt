package com.al4gms.unspray.presentation.adapters

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.al4gms.unspray.data.modelsui.OnBoardingPage
import com.al4gms.unspray.presentation.start.OnBoardingPageFragment

class OnBoardingAdapter(
    private val pages: Array<OnBoardingPage>,
    fragment: Fragment,
) : FragmentStateAdapter(fragment) {
    override fun getItemCount(): Int {
        return pages.size
    }

    override fun createFragment(position: Int): Fragment {
        val page = pages[position]
        return OnBoardingPageFragment.newInstance(
            page.title,
            page.ellipseDrawableRes,
            page.camerasDrawableRes,
            page.pageNumber,
        )
    }
}
