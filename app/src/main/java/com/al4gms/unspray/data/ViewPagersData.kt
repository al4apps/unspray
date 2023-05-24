package com.al4gms.unspray.data

import com.al4gms.unspray.presentation.profile.ContainerFragment
import com.al4gms.unspray.presentation.profile.ProfileFragment

object ViewPagersData {

    fun getUserContentPages(): Array<ContainerFragment> {
        return arrayOf(
            ContainerFragment.newInstance(ProfileFragment.PHOTOS_TAB_INDEX),
            ContainerFragment.newInstance(ProfileFragment.LIKES_TAB_INDEX),
            ContainerFragment.newInstance(ProfileFragment.COLLECTIONS_TAB_INDEX),
        )
    }
}
