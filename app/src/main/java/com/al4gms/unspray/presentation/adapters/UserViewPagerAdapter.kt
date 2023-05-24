package com.al4gms.unspray.presentation.adapters

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.al4gms.unspray.presentation.profile.ContainerFragment

class UserViewPagerAdapter(
    private val pages: Array<ContainerFragment>,
    fragment: Fragment,
) : FragmentStateAdapter(fragment) {
    override fun getItemCount(): Int = pages.size

    override fun createFragment(position: Int): Fragment {
        return pages[position]
    }
}
