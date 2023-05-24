package com.al4gms.unspray.presentation.profile

import android.os.Bundle
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import com.al4gms.unspray.R
import com.al4gms.unspray.databinding.FragmentContainerBinding
import com.al4gms.unspray.utils.ViewBindingFragment
import com.al4gms.unspray.utils.withArguments
import java.lang.Exception

class ContainerFragment :
    ViewBindingFragment<FragmentContainerBinding>(FragmentContainerBinding::inflate) {

    private lateinit var navController: NavController

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val pageType = requireArguments().getInt(KEY_PAGE_TYPE)
        setNavController(pageType)
        addBackPressCallback()
        initPages(pageType)
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        addBackPressCallback()
    }

    private fun setNavController(pageType: Int) {
        navController = when (pageType) {
            ProfileFragment.PHOTOS_TAB_INDEX -> {
                (childFragmentManager.findFragmentById(R.id.containerUserPhotos) as NavHostFragment)
                    .findNavController()
            }
            ProfileFragment.LIKES_TAB_INDEX -> {
                (childFragmentManager.findFragmentById(R.id.containerUserLikes) as NavHostFragment)
                    .findNavController()
            }
            ProfileFragment.COLLECTIONS_TAB_INDEX -> {
                (childFragmentManager.findFragmentById(R.id.containerUserCollections) as NavHostFragment)
                    .findNavController()
            }
            else -> throw Exception("Incorrect page type")
        }
    }

    private fun initPages(pageType: Int) {
        when (pageType) {
            ProfileFragment.PHOTOS_TAB_INDEX -> {
                binding.containerUserPhotos.visibility =
                    View.VISIBLE
            }
            ProfileFragment.LIKES_TAB_INDEX -> {
                binding.containerUserLikes.visibility = View.VISIBLE
            }
            ProfileFragment.COLLECTIONS_TAB_INDEX -> {
                binding.containerUserCollections.visibility =
                    View.VISIBLE
            }
        }
    }

    private fun addBackPressCallback() {
        val backCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                navController.popBackStack()
            }
        }
        navController.addOnDestinationChangedListener { controller, destination, arguments ->
            if (destination.id == R.id.userSPhotosFragment ||
                destination.id == R.id.userSLikesFragment ||
                destination.id == R.id.userCollectionsFragment
            ) {
                backCallback.remove()
            } else {
                requireActivity().onBackPressedDispatcher.addCallback(backCallback)
            }
        }
    }

    companion object {

        private const val KEY_PAGE_TYPE = "page_type"

        fun newInstance(pageType: Int) =
            ContainerFragment().withArguments {
                putInt(KEY_PAGE_TYPE, pageType)
            }
    }
}
