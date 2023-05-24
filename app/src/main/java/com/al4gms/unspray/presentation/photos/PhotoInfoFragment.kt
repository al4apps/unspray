package com.al4gms.unspray.presentation.photos

import android.app.DownloadManager
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.al4gms.unspray.R
import com.al4gms.unspray.data.ResultCodes
import com.al4gms.unspray.data.SavedValues
import com.al4gms.unspray.data.modelsui.content.Content
import com.al4gms.unspray.data.modelsui.content.PositionGeographic
import com.al4gms.unspray.databinding.FragmentPhotoBinding
import com.al4gms.unspray.service.DownloadReceiver
import com.al4gms.unspray.utils.DoubleClick
import com.al4gms.unspray.utils.DoubleClickListener
import com.al4gms.unspray.utils.ViewBindingFragment
import com.al4gms.unspray.utils.haveQ
import com.al4gms.unspray.utils.setImageWithGlide
import com.al4gms.unspray.utils.showLocation
import com.al4gms.unspray.utils.snackbar
import com.al4gms.unspray.utils.themeColor
import com.al4gms.unspray.utils.toast
import com.bumptech.glide.Glide
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.transition.MaterialContainerTransform
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PhotoInfoFragment : ViewBindingFragment<FragmentPhotoBinding>(FragmentPhotoBinding::inflate) {

    private val viewModel: PhotoViewModel by viewModels()
    private val args: PhotoInfoFragmentArgs by navArgs()
    private var downloadReceiver: DownloadReceiver? = null
    private lateinit var getPermissionsLauncher: ActivityResultLauncher<Array<String>>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedElementEnterTransition = MaterialContainerTransform().apply {
            drawingViewId = R.id.fragmentMainNavHostContainer
            duration = resources.getInteger(R.integer.unspray_motion_duration_large).toLong()
            scrimColor = Color.TRANSPARENT
            setAllContainerColors(requireContext().themeColor(com.google.android.material.R.attr.colorSurface))
        }
        initPermissionsResultListener()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val destinationId = findNavController().currentDestination?.id
        if (destinationId == R.id.photoInfoFragment || destinationId == R.id.photoInfoFragment2) {
            initToolbar()
        }
        if (savedInstanceState == null && viewModel.currentPhoto == null) {
            viewModel.getPhoto(args.photoId)
        }
        bindViewModel()
        binding.downloadButton.setOnClickListener {
            viewModel.downloadThisImage()
//            if (hasPermissions()) {
//                viewModel.downloadThisImage()
//            } else {
//                requestPermissions()
//            }
        }
        binding.swipeRefreshLayout.setOnRefreshListener {
            viewModel.getPhoto(args.photoId)
        }
    }

    private fun initToolbar() {
        binding.appBar.appBar.visibility = View.VISIBLE
        val toolbar = binding.appBar.toolbarWithShare
        toolbar.apply {
            setTitle(R.string.toolbar_photo_info_title)
            setNavigationIcon(R.drawable.ic_back)
            setOnMenuItemClickListener { menuItem ->
                if (menuItem.itemId == R.id.shareButton) {
                    shareLink()
                    true
                } else {
                    false
                }
            }
            setNavigationOnClickListener { findNavController().navigateUp() }
        }
    }

    private fun initPermissionsResultListener() {
        getPermissionsLauncher = registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions(),
        ) { permissionToGrantedMap ->
            if (permissionToGrantedMap.values.all { it }) {
                viewModel.downloadThisImage()
            } else {
                viewModel.permissionsDenied()
            }
        }
    }

    private fun hasPermissions(): Boolean {
        return PERMISSIONS.all {
            ActivityCompat.checkSelfPermission(
                requireContext(),
                it,
            ) == PackageManager.PERMISSION_GRANTED
        }
    }

    private fun requestPermissions() {
        getPermissionsLauncher.launch(PERMISSIONS.toTypedArray())
    }

    private fun shareLink() {
        viewModel.currentPhoto?.let { photo ->
            val link = SavedValues.getPhotoShareLink(photo.id)
            val intent = Intent(Intent.ACTION_SEND).apply {
                putExtra(Intent.EXTRA_TEXT, link)
                type = "text/plain"
            }
            val shareIntent = Intent.createChooser(intent, null)
            startActivity(shareIntent)
        }
    }

    private fun bindViewModel() {
        viewModel.photoDetailInfoLiveData.observe(viewLifecycleOwner) {
            setViews(it)
            binding.swipeRefreshLayout.isRefreshing = false
        }
        viewModel.photoInfoLiveData.observe(viewLifecycleOwner) { updatePhotoLikes(it) }
        viewModel.isLikingLiveData.observe(viewLifecycleOwner) { likingProgress(it) }
        viewModel.downloadIdLiveData.observe(viewLifecycleOwner) { downloadId ->
            Snackbar.make(binding.root, R.string.download_started_toast, Snackbar.LENGTH_LONG)
                .show()
            bindReceiver(downloadId)
        }
        viewModel.resultLiveData.observe(viewLifecycleOwner) { handleLoadResult(it) }
        SavedValues.connectionLiveData.observe(viewLifecycleOwner) {
            viewModel.hasConnection = it
        }
    }

    private fun handleLoadResult(result: Int) {
        when (result) {
            ResultCodes.RESULT_UNKNOWN -> toast(R.string.exception_unknown)
            ResultCodes.RESULT_CONNECTION_ERROR -> toast(R.string.exception_check_connection)
            ResultCodes.RESULT_MISSING_PERMISSIONS_FOR_REQUEST -> toast(R.string.exception_missing_permissions_for_request)
            ResultCodes.RESULT_PERMISSIONS_DENIED -> toast(R.string.no_permission_toast_text)
        }
    }

    private fun bindReceiver(downloadId: Long) {
        downloadReceiver = DownloadReceiver(downloadId)
        requireActivity().registerReceiver(
            downloadReceiver,
            IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE),
        )
        downloadReceiver?.intentLiveData?.observe(viewLifecycleOwner) { intent ->

            Snackbar.make(binding.root, R.string.photo_downloaded, Snackbar.LENGTH_LONG)
                .setAction(R.string.open_snackbar_action) {
                    requireActivity().startActivity(intent)
                }
                .show()
            requireActivity().unregisterReceiver(downloadReceiver)
            downloadReceiver = null
        }
    }

    private fun likingProgress(isLiked: Boolean) {
        binding.likedImageView.visibility = if (isLiked) {
            View.VISIBLE
        } else {
            View.INVISIBLE
        }
    }

    private fun updatePhotoLikes(photo: Content.Photo) {
        binding.likeCountTextView.text = photo.likes.toString()
        binding.likeImageView.setImageResource(
            if (photo.likedByUser) {
                R.drawable.ic_liked
            } else {
                R.drawable.ic_like_empty
            },
        )
        binding.likeImageView.setOnClickListener { likeThisPhoto(photo) }
        binding.likeImageView.setOnClickListener { likeThisPhoto(photo) }
    }

    private fun likeThisPhoto(photo: Content.Photo) {
        if (viewModel.hasConnection) {
            viewModel.likeOrUnLikePhoto(photo.id)
        } else {
            snackbar(binding.root, R.string.no_connection_toast)
        }
    }

    private fun showPhotoLocation(position: PositionGeographic) {
        if (position.latitude != null && position.longitude != null) {
            val intent = showLocation(position.latitude, position.longitude, null)
            startActivity(intent)
        }
    }

    private fun setViews(photo: Content.Photo) {
        val bioText =
            resources.getString(
                R.string.about_author_text,
                photo.user.username,
                photo.user.bio ?: "",
            )
        val exif = photo.exif

        binding.locationTextView.setOnClickListener {
            photo.location?.position?.let { showPhotoLocation(it) }
        }

        Glide.with(this@PhotoInfoFragment)
            .load(photo.urls.full)
            .placeholder(R.drawable.placeholder2)
            .into(binding.photoImageView)

        binding.nameTextView.text = photo.user.name
        binding.usernameTextView.text =
            resources.getString(R.string.at_mail_with_text, photo.user.username)
        binding.tagsTextView.text = photo.tags?.joinToString(" ") { "#" + it.title }
        binding.authorInfoTextView.text = bioText
        binding.downloadButton.text =
            resources.getString(R.string.download_with_count, photo.downloads)
        binding.avatarImageView.setImageWithGlide(this, photo.user.profileImage.small)

        if (exif?.make != null) {
            val exifText = resources.getString(
                R.string.exif_info,
                exif.make,
                exif.model,
                exif.exposureTime,
                exif.aperture.toString(),
                exif.focalLength,
                exif.iso,
            )
            binding.photoExifTextView.text = exifText
        } else {
            binding.photoExifTextView.visibility = View.GONE
        }
        val locationText = when {
            photo.location?.city != null && photo.location.country != null -> {
                resources.getString(
                    R.string.location_text_city_and_country,
                    photo.location.city,
                    photo.location.country,
                )
            }
            photo.location?.city != null || photo.location?.country != null -> {
                resources.getString(
                    R.string.location_text_city_or_country,
                    photo.location.city ?: "",
                    photo.location.country ?: "",
                )
            }
            else -> {
                binding.locationImageView.visibility = View.GONE
                ""
            }
        }
        binding.locationTextView.text = locationText
        binding.locationImageView.visibility = View.VISIBLE
        binding.downloadButton.visibility = View.VISIBLE

        binding.photoImageView.setOnClickListener(
            DoubleClick(object : DoubleClickListener {
                override fun onSingleClick(view: View?) {}
                override fun onDoubleClick(view: View?) {
                    viewModel.likeOrUnLikePhoto(photo.id)
                }
            }),
        )
    }

    override fun onDestroy() {
        super.onDestroy()
        downloadReceiver?.let { requireActivity().unregisterReceiver(it) }
    }

    companion object {
        private val PERMISSIONS = listOfNotNull(
            android.Manifest.permission.READ_EXTERNAL_STORAGE,
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE
                .takeIf { haveQ().not() },
        )
    }
}
