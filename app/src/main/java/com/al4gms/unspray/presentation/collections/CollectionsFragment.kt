package com.al4gms.unspray.presentation.collections

import android.content.Context
import android.net.ConnectivityManager
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
import com.al4gms.unspray.data.SavedValues
import com.al4gms.unspray.databinding.FragmentCollectionsBinding
import com.al4gms.unspray.presentation.adapters.ContentListAdapter
import com.al4gms.unspray.utils.PaginationScrollListener
import com.al4gms.unspray.utils.ViewBindingFragment
import com.al4gms.unspray.utils.haveM
import com.al4gms.unspray.utils.toast
import com.google.android.material.transition.MaterialElevationScale
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CollectionsFragment :
    ViewBindingFragment<FragmentCollectionsBinding>(FragmentCollectionsBinding::inflate) {

    private val viewModel: CollectionsViewModel by viewModels()
    private var contentListAdapter by autoCleared<ContentListAdapter>()
    private lateinit var connectivityManager: ConnectivityManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        connectivityManager = if (haveM()) {
            requireActivity().getSystemService(ConnectivityManager::class.java) as ConnectivityManager
        } else {
            requireActivity().getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        }
        val hasConnection = if (haveM()) {
            connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork) != null
        } else {
            val activeNetwork = connectivityManager.activeNetworkInfo
            activeNetwork?.isConnectedOrConnecting == true
        }
        viewModel.hasConnection = hasConnection
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        postponeEnterTransition()
        view.doOnPreDraw { startPostponedEnterTransition() }

        initToolbar()
        initCollectionsAdapter()
        bindViewModel()
        if (savedInstanceState == null || contentListAdapter.items.isEmpty()) {
            viewModel.getCollections()
        }
    }

    private fun initToolbar() {
        binding.appBar.toolbar.setTitle(R.string.toolbar_collections_section_title)
    }

    private fun bindViewModel() {
        viewModel.collectionsLiveData.observe(viewLifecycleOwner) {
            contentListAdapter.items = it
        }
        viewModel.isLoadingLiveData.observe(viewLifecycleOwner) { showProgress(it) }
        viewModel.resultLiveData.observe(viewLifecycleOwner) {
            handleLoadResult(it)
        }
        SavedValues.connectionLiveData.observe(viewLifecycleOwner) { isAvailable ->
            if (isAvailable && viewModel.hasConnection.not()) {
                viewModel.getCollections()
            }
            viewModel.hasConnection = isAvailable
        }
    }

    private fun handleLoadResult(result: Int) {
        when (result) {
            ResultCodes.RESULT_UNKNOWN -> toast(R.string.exception_unknown)
            ResultCodes.RESULT_CONNECTION_ERROR -> toast(R.string.exception_check_connection)
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
                    viewModel.currentPage++
                    viewModel.getCollections()
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
            CollectionsFragmentDirections.actionCollectionsFragmentToCollectionFragment(collectionId)
        findNavController().navigate(action, extras)
    }
}
