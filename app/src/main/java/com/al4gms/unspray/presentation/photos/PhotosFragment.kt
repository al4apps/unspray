package com.al4gms.unspray.presentation.photos

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.widget.SearchView
import androidx.core.view.doOnPreDraw
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import androidx.paging.PagingData
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.al4gms.flowhw.utils.autoCleared
import com.al4gms.unspray.R
import com.al4gms.unspray.data.ResultCodes.RESULT_MISSING_PERMISSIONS_FOR_REQUEST
import com.al4gms.unspray.data.ResultCodes.RESULT_NO_CONNECTION_DB_EMPTY
import com.al4gms.unspray.data.ResultCodes.RESULT_NO_CONNECTION_LOADED_FROM_DB
import com.al4gms.unspray.data.ResultCodes.RESULT_UNKNOWN
import com.al4gms.unspray.data.SavedValues
import com.al4gms.unspray.data.modelsui.content.Content
import com.al4gms.unspray.databinding.FragmentPhotosBinding
import com.al4gms.unspray.presentation.adapters.ContentPagingAdapter
import com.al4gms.unspray.presentation.decor.ItemOffsetDecoration
import com.al4gms.unspray.utils.ViewBindingFragment
import com.al4gms.unspray.utils.getNetworkConnectionState
import com.al4gms.unspray.utils.onQueryTextListenerFlow
import com.al4gms.unspray.utils.snackbar
import com.al4gms.unspray.utils.themeColor
import com.al4gms.unspray.utils.toast
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.transition.MaterialElevationScale
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import timber.log.Timber

@AndroidEntryPoint
class PhotosFragment :
    ViewBindingFragment<FragmentPhotosBinding>(FragmentPhotosBinding::inflate) {

    private val viewModel: PhotosViewModel by viewModels()
    private var contentPagingAdapter by autoCleared<ContentPagingAdapter>()

    private val backCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            viewModel.switchSearchMode(false)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        postponeEnterTransition()
        view.doOnPreDraw { startPostponedEnterTransition() }

        initToolbar()
        initPagingAdapter()
        bindViewModel()
        initRefreshLayout()
        initConnectionState()
    }

    override fun onResume() {
        super.onResume()
        initToolbar()
    }

    private fun initRefreshLayout() {
        binding.swipeRefreshLayout.apply {
            setColorSchemeColors(requireActivity().themeColor(com.google.android.material.R.attr.colorSecondary))
            setOnRefreshListener {
                val searchView =
                    binding.appBar.toolbarWithSearch.menu.findItem(R.id.searchBar).actionView as SearchView
                if (viewModel.isSearchMode) {
                    viewModel.refreshSearchFeed(searchView.query.toString())
                } else {
                    viewModel.refreshFeed()
                }
            }
        }
    }

    private fun initToolbar() {
        val toolbar = binding.appBar.toolbarWithSearch
        if (viewModel.isSearchMode.not()) {
            toolbar.setTitle(R.string.toolbar_photos_section_title)
        } else {
            onSearchModeChanged(true)
        }
        toolbar.setOnMenuItemClickListener { menuItem ->
            if (menuItem.itemId == R.id.searchBar) {
                viewModel.switchSearchMode(true)
                val searchView = menuItem.actionView as SearchView
                searchView.queryHint = getString(R.string.search_hint)
                viewModel.searchFlow(searchView.onQueryTextListenerFlow())
                true
            } else {
                false
            }
        }
    }

    private fun initConnectionState() {
        SavedValues.connectionLiveData.observe(viewLifecycleOwner) { isAvailable ->
            toast(isAvailable.toString())
            val isConnected = getNetworkConnectionState()
            toast(isConnected.toString())
            if (isAvailable && viewModel.hasConnection.not() && isConnected) {
                Snackbar.make(
                    binding.root,
                    R.string.connection_is_available,
                    Snackbar.LENGTH_LONG,
                )
                    .setAction(R.string.do_refresh_snackbar_action) { viewModel.refreshFeed() }
                    .show()
            }
            viewModel.hasConnection = isConnected
        }
    }

    private fun bindViewModel() {
        viewModel.apply {
            photosLiveData.observe(viewLifecycleOwner) { data ->
                viewLifecycleOwner.lifecycleScope.launch {
                    contentPagingAdapter.submitData(data)
                }
                binding.swipeRefreshLayout.isRefreshing = false
                showNoPhotosPlaceHolder(data == PagingData.empty<Content.Photo>())
            }
            isLoadingLiveData.observe(viewLifecycleOwner, ::showProgress)
            resultLiveData.observe(viewLifecycleOwner, ::handleLoadResult)
            isSearchModeLiveData.observe(viewLifecycleOwner, ::onSearchModeChanged)
        }
    }

    private fun onSearchModeChanged(isSearch: Boolean) {
        binding.appBar.toolbarWithSearch.apply {
            if (isSearch) {
                setTitle(R.string.toolbar_search_mode_title)
                setNavigationIcon(R.drawable.ic_back)
                setNavigationOnClickListener {
                    viewModel.switchSearchMode(false)
                    navigationIcon = null
                }
                requireActivity().onBackPressedDispatcher.addCallback(backCallback)
            } else {
                setTitle(R.string.toolbar_photos_section_title)
                navigationIcon = null
                collapseActionView()
                backCallback.remove()
            }
        }
    }

    private fun handleLoadResult(result: Int) {
        when (result) {
            RESULT_NO_CONNECTION_LOADED_FROM_DB -> snackbar(
                binding.root,
                R.string.no_connection_load_from_db,
            )
            RESULT_NO_CONNECTION_DB_EMPTY -> snackbar(binding.root, R.string.no_connection_toast)
            RESULT_UNKNOWN -> toast(R.string.exception_unknown)
            RESULT_MISSING_PERMISSIONS_FOR_REQUEST -> toast(R.string.exception_missing_permissions_for_request)
        }
    }

    private fun showNoPhotosPlaceHolder(isEmpty: Boolean) {
        if (isEmpty && contentPagingAdapter.itemCount == 0) {
            binding.noPhotoPlaceholderContainer.visibility = View.VISIBLE
        } else {
            binding.noPhotoPlaceholderContainer.visibility = View.GONE
        }
    }

    private fun showProgress(isLoading: Boolean) {
        binding.progressHorizontal.visibility = if (isLoading) {
            View.VISIBLE
        } else {
            View.GONE
        }
    }

    private fun initPagingAdapter() {
        contentPagingAdapter = ContentPagingAdapter { photoId, itemView ->
            navigateToPhotoInfo(itemView, photoId)
        }
        with(binding.photosRecyclerView) {
            adapter = contentPagingAdapter
            layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
            addItemDecoration(ItemOffsetDecoration(requireContext()))
            setHasFixedSize(false)
        }
    }

    override fun onPause() {
        super.onPause()
        backCallback.remove()
    }

    private fun navigateToPhotoInfo(itemView: View, photoId: String) {
        exitTransition = MaterialElevationScale(false).apply {
            duration = resources.getInteger(R.integer.unspray_motion_duration_large).toLong()
        }
        reenterTransition = MaterialElevationScale(true).apply {
            duration = resources.getInteger(R.integer.unspray_motion_duration_large).toLong()
        }
        val photoInfoTransitionName = getString(R.string.photo_info_transition_name)
        val extras = FragmentNavigatorExtras(itemView to photoInfoTransitionName)
        val action = PhotosFragmentDirections.actionPhotosFragmentToPhotoInfoFragment(photoId)
        findNavController().navigate(action, extras)
    }
}
