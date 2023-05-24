package com.al4gms.unspray.presentation.profile.collections

import android.os.Bundle
import android.view.View
import androidx.core.view.doOnPreDraw
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.al4gms.flowhw.utils.autoCleared
import com.al4gms.unspray.R
import com.al4gms.unspray.data.ResultCodes
import com.al4gms.unspray.data.ResultCodes.RESULT_NO_COLLECTIONS
import com.al4gms.unspray.data.ResultCodes.RESULT_UNKNOWN
import com.al4gms.unspray.data.SavedValues
import com.al4gms.unspray.databinding.FragmentCollectionsBinding
import com.al4gms.unspray.presentation.adapters.ContentListAdapter
import com.al4gms.unspray.utils.PaginationScrollListener
import com.al4gms.unspray.utils.ViewBindingFragment
import com.al4gms.unspray.utils.toast
import com.google.android.material.transition.MaterialElevationScale
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class UserCollectionsFragment :
    ViewBindingFragment<FragmentCollectionsBinding>(FragmentCollectionsBinding::inflate) {

    private val viewModel: UserCollectionsViewModel by viewModels()
    private var contentListAdapter by autoCleared<ContentListAdapter>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.appBar.toolbar.visibility = View.GONE
        postponeEnterTransition()
        view.doOnPreDraw { startPostponedEnterTransition() }
        initCollectionsAdapter()
        bindViewModel()
        if (savedInstanceState == null && viewModel.currentList.isEmpty()) {
            viewModel.getUserCollections()
        }
    }

    private fun bindViewModel() {
        viewModel.collectionsLiveData.observe(viewLifecycleOwner) {
            contentListAdapter.items = it
            viewModel.currentList = it
            if (it.isEmpty()) {
                binding.emptyPlaceholderImageView.visibility = View.VISIBLE
            } else {
                binding.emptyPlaceholderImageView.visibility = View.GONE
            }
        }
        viewModel.isLoadingLiveData.observe(viewLifecycleOwner) { showProgress(it) }
        viewModel.resultLiveData.observe(viewLifecycleOwner) {
            handleResult(it)
        }
        SavedValues.connectionLiveData.observe(viewLifecycleOwner) { isAvailable ->
            if (isAvailable && contentListAdapter.items.isEmpty() && viewModel.isLoading.not()) {
                viewModel.refreshList()
            }
        }
    }

    private fun handleResult(result: Int) {
        when (result) {
            RESULT_NO_COLLECTIONS -> {
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

    private fun showProgress(isLoading: Boolean) {
        binding.progressHorizontal.visibility = if (isLoading) {
            View.VISIBLE
        } else {
            View.GONE
        }
    }

    private fun initCollectionsAdapter() {
        contentListAdapter = ContentListAdapter { collectionId, itemView ->
            navigateToCollectionInfo(itemView, collectionId)
        }
        with(binding.collectionsRecyclerView) {
            adapter = contentListAdapter
            layoutManager = LinearLayoutManager(requireContext())
            setHasFixedSize(true)

            this.addOnScrollListener(object : PaginationScrollListener(
                layoutManager as LinearLayoutManager,
            ) {
                override fun loadMoreItems() {
                    viewModel.getUserCollections()
                }

                override val isLastPage: Boolean
                    get() = viewModel.isLastPage
                override val isLoading: Boolean
                    get() = viewModel.isLoading
            })
        }
    }

    private fun navigateToCollectionInfo(itemView: View, collectionId: String) {
        exitTransition = MaterialElevationScale(false).apply {
            duration = resources.getInteger(R.integer.unspray_motion_duration_large).toLong()
        }
        reenterTransition = MaterialElevationScale(true).apply {
            duration = resources.getInteger(R.integer.unspray_motion_duration_large).toLong()
        }
        val transitionName = getString(R.string.collection_transition_name)
        val extras = FragmentNavigatorExtras(itemView to transitionName)
        val action =
            UserCollectionsFragmentDirections.actionUserCollectionsFragmentToUserSCollectionFragment(
                collectionId,
            )
        findNavController().navigate(action, extras)
    }
}
