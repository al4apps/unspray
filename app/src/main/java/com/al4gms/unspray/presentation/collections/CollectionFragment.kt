package com.al4gms.unspray.presentation.collections

import android.content.Intent
import android.graphics.Color
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
import com.al4gms.unspray.databinding.FragmentCollectionBinding
import com.al4gms.unspray.presentation.adapters.ContentListAdapter
import com.al4gms.unspray.utils.PaginationScrollListener
import com.al4gms.unspray.utils.ViewBindingFragment
import com.al4gms.unspray.utils.themeColor
import com.al4gms.unspray.utils.toast
import com.google.android.material.transition.MaterialContainerTransform
import com.google.android.material.transition.MaterialElevationScale
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CollectionFragment :
    ViewBindingFragment<FragmentCollectionBinding>(FragmentCollectionBinding::inflate) {

    private val viewModel: CollectionViewModel by viewModels()
    private var photosAdapter by autoCleared<ContentListAdapter>()
    private val args: CollectionFragmentArgs by navArgs()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedElementEnterTransition = MaterialContainerTransform().apply {
            drawingViewId = R.id.fragmentMainNavHostContainer
            duration = resources.getInteger(R.integer.unspray_motion_duration_large).toLong()
            scrimColor = Color.TRANSPARENT
            setAllContainerColors(requireContext().themeColor(com.google.android.material.R.attr.colorSurface))
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        postponeEnterTransition()
        view.doOnPreDraw { startPostponedEnterTransition() }
        viewModel.collectionId = args.collectionId
        initToolbar()
        initAdapter()
        bindViewModel()
    }

    private fun initToolbar() {
        binding.appBar.appBar.visibility = View.VISIBLE
        binding.appBar.toolbarWithShare.apply {
            setTitle(R.string.toolbar_collection_info_title)
            setNavigationIcon(R.drawable.ic_back)
            setNavigationIconTint(requireContext().themeColor(com.google.android.material.R.attr.colorOnSurface))
            setNavigationOnClickListener { findNavController().navigateUp() }
            setOnMenuItemClickListener { menuItem ->
                when (menuItem.itemId) {
                    R.id.shareButton -> shareLink()
                }
                true
            }
        }
    }

    private fun shareLink() {
        viewModel.collectionId.let { collectionID ->
            val link = SavedValues.getCollectionsShareLink(collectionID)
            val intent = Intent(Intent.ACTION_SEND).apply {
                putExtra(Intent.EXTRA_TEXT, link)
                type = "text/plain"
            }
            val shareIntent = Intent.createChooser(intent, null)
            startActivity(shareIntent)
        }
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
                    viewModel.currentPage++
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
            CollectionFragmentDirections.actionCollectionFragmentToPhotoInfoFragment2(photoId)

        findNavController().navigate(action, extras)
    }

    private fun bindViewModel() {
        viewModel.getPhotosInCollection()
        viewModel.getCollectionInfo()

        viewModel.collectionInfo.observe(viewLifecycleOwner) { showViews(it) }
        viewModel.photosLiveData.observe(viewLifecycleOwner) { photos ->
            photosAdapter.items = photos
            if (photos.isEmpty()) {
                binding.noPhotoPlaceholderContainer.visibility = View.VISIBLE
            } else {
                binding.noPhotoPlaceholderContainer.visibility = View.GONE
            }
        }
        viewModel.resultLiveData.observe(viewLifecycleOwner) { handleLoadResult(it) }
        viewModel.isLoadingLiveData.observe(viewLifecycleOwner) { showProgress(it) }
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

    private fun showViews(collection: Content.Collection?) {
        if (collection == null) return
        if (collection.private) toast("private")
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
