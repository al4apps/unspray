package com.al4gms.unspray.presentation.profile.collections

import android.os.Bundle
import android.view.View
import androidx.core.view.doOnPreDraw
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.al4gms.flowhw.utils.autoCleared
import com.al4gms.unspray.R
import com.al4gms.unspray.data.ResultCodes
import com.al4gms.unspray.data.SavedValues
import com.al4gms.unspray.data.modelsui.content.Content
import com.al4gms.unspray.data.setImageWithGlide
import com.al4gms.unspray.databinding.FragmentCollectionNoToolbarBinding
import com.al4gms.unspray.presentation.adapters.ContentListAdapter
import com.al4gms.unspray.utils.PaginationScrollListener
import com.al4gms.unspray.utils.ViewBindingFragment
import com.al4gms.unspray.utils.toast
import com.google.android.material.transition.MaterialElevationScale
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class UserSCollectionFragment :
    ViewBindingFragment<FragmentCollectionNoToolbarBinding>(FragmentCollectionNoToolbarBinding::inflate) {

    private val viewModel: UserCollectionsViewModel by viewModels()
    private var photosAdapter by autoCleared<ContentListAdapter>()
    private val args: UserSCollectionFragmentArgs by navArgs()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        postponeEnterTransition()
        view.doOnPreDraw { startPostponedEnterTransition() }

        initAdapter()
        viewModel.collectionId = args.collectionId
        bindViewModel()
    }
    private fun initAdapter() {
        photosAdapter = ContentListAdapter { photoId, itemView ->
            navigateToPhotoInfo(itemView, photoId)
        }
        with(binding.photosRecyclerView) {
            adapter = photosAdapter
            layoutManager = LinearLayoutManager(requireContext())
            setHasFixedSize(false)

            this.addOnScrollListener(object : PaginationScrollListener(
                layoutManager as LinearLayoutManager,
            ) {
                override fun loadMoreItems() {
                    viewModel.getPhotosInCollection()
                }

                override val isLastPage: Boolean
                    get() = viewModel.isLastPage
                override val isLoading: Boolean
                    get() = viewModel.isLoading
            })
        }
    }

    private fun navigateToPhotoInfo(itemView: View, photoId: String) {
        exitTransition = MaterialElevationScale(false).apply {
            duration = resources.getInteger(R.integer.unspray_motion_duration_large).toLong()
        }
        reenterTransition = MaterialElevationScale(true).apply {
            duration = resources.getInteger(R.integer.unspray_motion_duration_large).toLong()
        }
        val photoTransitionName = getString(R.string.photo_info_transition_name)
        val extras = FragmentNavigatorExtras(itemView to photoTransitionName)
        val action =
            UserSCollectionFragmentDirections.actionUserSCollectionFragmentToPhotoInfoFragment3(photoId)
        findNavController().navigate(action, extras)
    }

    private fun bindViewModel() {
        viewModel.getCollection()
        viewModel.getPhotosInCollection()

        viewModel.collectionInfo.observe(viewLifecycleOwner) { showViews(it) }
        viewModel.photosLiveData.observe(viewLifecycleOwner) { photos ->
            photosAdapter.items = photos
            if (photos.isEmpty()) {
                binding.noPhotoPlaceholderContainer.visibility = View.VISIBLE
            } else {
                binding.noPhotoPlaceholderContainer.visibility = View.GONE
            }
        }
        viewModel.resultLiveData.observe(viewLifecycleOwner) { handleResult(it) }
        viewModel.isLoadingLiveData.observe(viewLifecycleOwner) { showProgress(it) }
        SavedValues.connectionLiveData.observe(viewLifecycleOwner) { isAvailable ->
            if (isAvailable) viewModel.refreshCollection()
        }
    }

    private fun handleResult(result: Int) {
        when (result) {
            ResultCodes.RESULT_UNKNOWN -> toast(R.string.exception_unknown)
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

    private fun showViews(collection: Content.Collection?) {
        if (collection == null) return
        binding.coverPhotoImageView.setImageWithGlide(this, collection.coverPhoto.urls.small)
        binding.titleTextView.text = collection.title
        if (collection.description != null) {
            binding.descriptionTextView.visibility = View.VISIBLE
            binding.descriptionTextView.text = collection.description
        }
        if (collection.tags != null) {
            binding.tagsTextView.visibility = View.VISIBLE
            binding.tagsTextView.text = collection.tags.joinToString(" ") { "#" + it.title }
        }
        val imgCountAndUsername = resources.getString(
            R.string.n_photos_by_author,
            collection.totalPhotos,
            collection.user.username,
        )
        binding.imageCountAndUsernameTextView.text = imgCountAndUsername
    }
}
