package com.al4gms.unspray.presentation.profile.photos

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.al4gms.flowhw.utils.autoCleared
import com.al4gms.unspray.R
import com.al4gms.unspray.data.ResultCodes
import com.al4gms.unspray.data.ResultCodes.RESULT_NO_PHOTOS
import com.al4gms.unspray.data.ResultCodes.RESULT_UNKNOWN
import com.al4gms.unspray.data.SavedValues
import com.al4gms.unspray.databinding.FragmentUserPhotosBinding
import com.al4gms.unspray.presentation.adapters.UserSContentListAdapter
import com.al4gms.unspray.presentation.decor.ItemOffsetDecoration
import com.al4gms.unspray.utils.PaginationScrollListener
import com.al4gms.unspray.utils.ViewBindingFragment
import com.al4gms.unspray.utils.toast
import com.google.android.material.transition.MaterialElevationScale
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class UserSPhotosFragment :
    ViewBindingFragment<FragmentUserPhotosBinding>(FragmentUserPhotosBinding::inflate) {

    private val viewModel: UserPhotosViewModel by viewModels()
    private var contentListAdapter by autoCleared<UserSContentListAdapter>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initPhotosAdapter()
        bindViewModel()
        if (savedInstanceState == null && viewModel.currentList.isEmpty()) {
            viewModel.getUserSContent(UserPhotosViewModel.TYPE_PHOTOS)
        }
    }

    private fun bindViewModel() {
        viewModel.photosLiveData.observe(viewLifecycleOwner) {
            contentListAdapter.items = it
            viewModel.currentList = it
            onEmptyList(it.isEmpty())
        }
        viewModel.isLoadingLiveData.observe(viewLifecycleOwner) { showProgress(it) }
        viewModel.resultLiveData.observe(viewLifecycleOwner) { handleResult(it) }
        SavedValues.connectionLiveData.observe(viewLifecycleOwner) { isAvailable ->
            if (isAvailable && contentListAdapter.items.isEmpty() && viewModel.isLoading.not()) {
                viewModel.refreshList(UserPhotosViewModel.TYPE_PHOTOS)
            }
        }
    }

    private fun handleResult(result: Int) {
        when (result) {
            RESULT_NO_PHOTOS -> {
                binding.emptyPlaceholderTextView.visibility = View.VISIBLE
                binding.emptyPlaceholderImageView.visibility = View.VISIBLE
            }
            RESULT_UNKNOWN -> {
                binding.emptyPlaceholderTextView.visibility = View.GONE
                binding.emptyPlaceholderImageView.visibility = View.VISIBLE
            }
            ResultCodes.RESULT_MISSING_PERMISSIONS_FOR_REQUEST -> toast(R.string.exception_missing_permissions_for_request)
        }
    }

    private fun onEmptyList(isEmpty: Boolean) {
        if (isEmpty) {
            binding.emptyPlaceholderImageView.visibility = View.VISIBLE
        } else {
            binding.emptyPlaceholderImageView.visibility = View.GONE
        }
    }

    private fun showProgress(isLoading: Boolean) {
        binding.progressHorizontal.visibility = if (isLoading) {
            View.VISIBLE
        } else {
            View.GONE
        }
    }

    private fun initPhotosAdapter() {
        contentListAdapter = UserSContentListAdapter { photoId, itemView ->
            navigateToPhotoInfo(itemView, photoId)
        }
        with(binding.photosRecyclerView) {
            adapter = contentListAdapter
            layoutManager = LinearLayoutManager(requireContext())
            addItemDecoration(ItemOffsetDecoration(requireContext()))
            setHasFixedSize(true)

            binding.photosRecyclerView.addOnScrollListener(
                object : PaginationScrollListener(
                    layoutManager as LinearLayoutManager,
                ) {
                    override fun loadMoreItems() {
                        viewModel.currentPage++
                        viewModel.getUserSContent(UserPhotosViewModel.TYPE_PHOTOS)
                    }

                    override val isLastPage: Boolean
                        get() = viewModel.isLastPage
                    override val isLoading: Boolean
                        get() = viewModel.isLoading
                },
            )
        }
    }

    private fun navigateToPhotoInfo(itemView: View, photoId: String) {
        exitTransition = MaterialElevationScale(false).apply {
            duration =
                resources.getInteger(R.integer.unspray_motion_duration_large)
                    .toLong()
        }
        reenterTransition = MaterialElevationScale(true).apply {
            duration =
                resources.getInteger(R.integer.unspray_motion_duration_large)
                    .toLong()
        }
        val transitionName = getString(R.string.photo_info_transition_name)
        val extras = FragmentNavigatorExtras(itemView to transitionName)
        val action =
            UserSPhotosFragmentDirections.actionUserSPhotosFragmentToUserPhotoInfoFragment(photoId)
        findNavController().navigate(action, extras)
    }
}
