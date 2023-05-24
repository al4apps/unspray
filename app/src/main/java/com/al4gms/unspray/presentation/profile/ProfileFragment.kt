package com.al4gms.unspray.presentation.profile

import android.content.Context
import android.net.ConnectivityManager
import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import com.al4gms.unspray.R
import com.al4gms.unspray.data.ResultCodes.RESULT_CONNECTION_ERROR
import com.al4gms.unspray.data.ResultCodes.RESULT_MISSING_PERMISSIONS_FOR_REQUEST
import com.al4gms.unspray.data.ResultCodes.RESULT_NO_USER_INFO
import com.al4gms.unspray.data.ResultCodes.RESULT_UNKNOWN
import com.al4gms.unspray.data.SavedValues
import com.al4gms.unspray.data.ViewPagersData
import com.al4gms.unspray.data.modelsui.user.User
import com.al4gms.unspray.databinding.FragmentProfileBinding
import com.al4gms.unspray.presentation.MainFragment
import com.al4gms.unspray.presentation.adapters.UserViewPagerAdapter
import com.al4gms.unspray.utils.ViewBindingFragment
import com.al4gms.unspray.utils.haveM
import com.al4gms.unspray.utils.setImageWithGlide
import com.al4gms.unspray.utils.snackbar
import com.al4gms.unspray.utils.toast
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProfileFragment :
    ViewBindingFragment<FragmentProfileBinding>(FragmentProfileBinding::inflate) {

    private val viewModel: ProfileViewModel by viewModels()
    private lateinit var viewPagerAdapter: UserViewPagerAdapter
    private lateinit var connectivityManager: ConnectivityManager
    private var snack: Snackbar? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        connectivityManager = if (haveM()) {
            requireActivity().getSystemService(ConnectivityManager::class.java) as ConnectivityManager
        } else {
            requireActivity().getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        }
        viewModel.hasConnection = if (haveM()) {
            connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork) != null
        } else {
            val activeNetwork = connectivityManager.activeNetworkInfo
            activeNetwork?.isConnectedOrConnecting == true
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initToolbar()
        bindViewModel()
        bindViewPager()
    }

    private fun initToolbar() {
        binding.appBar.toolbarWithLogout.setTitle(R.string.toolbar_profile_section_title)
        binding.appBar.toolbarWithLogout.setOnMenuItemClickListener { menuItem ->
            if (menuItem.itemId == R.id.logoutButton) {
                logoutProcess()
            }
            true
        }
    }

    private fun logoutProcess() {
        binding.touchView.visibility = View.VISIBLE
        snack = Snackbar.make(
            binding.root,
            R.string.logout_are_you_sure,
            Snackbar.LENGTH_INDEFINITE,
        )
            .setAction(R.string.logout) {
                viewModel.logout()
                binding.touchView.visibility = View.GONE
            }
        snack?.show()
        binding.touchView.setOnClickListener {
            snack?.dismiss()
            it.visibility = View.GONE
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        snack?.dismiss()
    }

    private fun handleResult(result: Int) {
        when (result) {
            RESULT_LOGOUT_SUCCESS -> {
//                findNavController().navigate(ProfileFragmentDirections.actionProfileFragmentToNavGraph())
//                requireActivity().findNavController(R.id.fragmentMainNavHostContainer)
//                    .navigate(R.id.action_mainFragment_to_authFragment2)

                Navigation.findNavController(requireActivity(), R.id.fragmentMainNavHostContainer)
                    .navigate(R.id.nav_graph)

//                (parentFragmentManager.findFragmentById(R.id.fragmentMainNavHostContainer) as NavHostFragment)
//                    .navController
//                    .navigate(R.id.action_mainFragment_to_authFragment2)
//                (parentFragment as NavHostFragment).findNavController().navigate(ProfileFragmentDirections.actionProfileFragmentToNavGraph())
            }
            RESULT_MISSING_PERMISSIONS_FOR_REQUEST -> {
                snackbar(binding.root, R.string.exception_missing_permissions_for_request)
            }
            RESULT_CONNECTION_ERROR -> snackbar(binding.root, R.string.exception_check_connection)
            RESULT_NO_USER_INFO -> setUserInfoPlaceholder(false)
            RESULT_UNKNOWN -> toast(R.string.exception_unknown)
        }
    }

    private fun setUserInfoPlaceholder(hasUserInfo: Boolean) {
        if (hasUserInfo.not()) {
            binding.containerMatchParent.visibility = View.GONE
            binding.noUserInfoContainer.visibility = View.VISIBLE
        } else {
            binding.containerMatchParent.visibility = View.VISIBLE
            binding.noUserInfoContainer.visibility = View.GONE
        }
    }

    private fun bindViewPager() {
        viewPagerAdapter = UserViewPagerAdapter(ViewPagersData.getUserContentPages(), this)
        binding.viewPager.adapter = viewPagerAdapter

        TabLayoutMediator(binding.tabLayout, binding.viewPager) { _, _ -> }.attach()
    }

    private fun bindViewModel() {
        viewModel.getUserInfoNetwork()
        viewModel.userInfoLiveData.observe(viewLifecycleOwner) {
            setUserInfoViews(it)
            setUserInfoPlaceholder(true)
        }
        viewModel.isLoadingLiveData.observe(viewLifecycleOwner) { showProgress(it) }
        viewModel.resultLiveData.observe(viewLifecycleOwner) { handleResult(it) }
        SavedValues.connectionLiveData.observe(viewLifecycleOwner) { isAvailable ->
            if (isAvailable) {
                if (SavedValues.currentUser == null) viewModel.getUserInfoNetwork()
            }
            viewModel.hasConnection = isAvailable
        }
    }

    private fun showProgress(isLoading: Boolean) {
        if (isLoading) {
            binding.containerMatchParent.visibility = View.INVISIBLE
            binding.progressHorizontal.visibility = View.VISIBLE
        } else {
            binding.containerMatchParent.visibility = View.VISIBLE
            binding.progressHorizontal.visibility = View.GONE
        }
    }

    private fun setUserInfoViews(user: User) {
        binding.avatarImageView.setImageWithGlide(
            this,
            user.profileImage.large,
            R.drawable.placeholder2,
        )
        if (user.name.length >= 18) binding.nameTextView.textSize = 27F
        if (user.name.length >= 23) binding.nameTextView.textSize = 22F
        binding.nameTextView.text = user.name
        binding.usernameTextView.text =
            resources.getString(R.string.at_mail_with_text, user.username)
        if (user.bio?.isNotEmpty() == true) {
            binding.bioTextView.text = user.bio
            binding.bioTextView.visibility = View.VISIBLE
        }
        user.email?.let {
            binding.eMailTextView.text = it
            binding.containerMail.visibility = View.VISIBLE
        }
        if (user.location != null) {
            binding.containerLocation.visibility = View.VISIBLE
            binding.locationTextView.text = user.location
        }
        if (user.downloads!! > 0) {
            binding.containerDownloads.visibility = View.VISIBLE
            binding.downloadCountTextView.text = user.downloads.toString()
        }
        binding.tabLayout.getTabAt(PHOTOS_TAB_INDEX)?.text =
            resources.getString(R.string.photos_tab_title_on_profile, user.totalPhotos)
        binding.tabLayout.getTabAt(LIKES_TAB_INDEX)?.text =
            resources.getString(R.string.likes_tab_title_on_profile, user.totalLikes)
        binding.tabLayout.getTabAt(COLLECTIONS_TAB_INDEX)?.text =
            resources.getString(R.string.collections_tab_title_on_profile, user.totalCollections)
    }

    companion object {
        const val PHOTOS_TAB_INDEX = 0
        const val LIKES_TAB_INDEX = 1
        const val COLLECTIONS_TAB_INDEX = 2

        const val RESULT_LOGOUT_SUCCESS = 23153
    }
}
