package com.al4gms.unspray.presentation

import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.fragment.app.FragmentResultListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asFlow
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.al4gms.unspray.R
import com.al4gms.unspray.data.SavedValues
import com.al4gms.unspray.databinding.FragmentMainBinding
import com.al4gms.unspray.presentation.profile.ProfileViewModel
import com.al4gms.unspray.service.NetworkStateChangedCallback
import com.al4gms.unspray.utils.ViewBindingFragment
import com.al4gms.unspray.utils.getConnectivityManager
import com.al4gms.unspray.utils.hasConnection
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

@AndroidEntryPoint
class MainFragment : ViewBindingFragment<FragmentMainBinding>(FragmentMainBinding::inflate) {

    private val profileViewModel: ProfileViewModel by viewModels()
    private lateinit var connectivityManager: ConnectivityManager

    private val networkRequest = NetworkRequest.Builder()
        .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
        .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
        .addTransportType(NetworkCapabilities.TRANSPORT_CELLULAR)
        .build()

    private val networkCallback = object : NetworkStateChangedCallback() {
        override fun onNetworkStateChanged(isAvailable: Boolean) {
            sendConnectionState(isAvailable)
            if (isAvailable && (SavedValues.currentUser == null)) profileViewModel.cacheCurrentUser()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        connectivityManager = requireActivity().getConnectivityManager()
        connectivityManager.hasConnection().apply {
            sendConnectionState(this)
        }
        Timber.d("MainFragment OnCreate ${this}")
    }

    override fun onResume() {
        super.onResume()
        connectivityManager.registerNetworkCallback(
            networkRequest,
            networkCallback,
        )
    }

    override fun onDestroy() {
        super.onDestroy()
        Timber.d("MainFragment OnDestroy ${this}")
    }

    override fun onPause() {
        super.onPause()
        connectivityManager.unregisterNetworkCallback(networkCallback)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initBottomBar()
        if (profileViewModel.hasConnection && (SavedValues.currentUser == null)) {
            profileViewModel.cacheCurrentUser()
        }
        profileViewModel.getRequestCount()
        profileViewModel.counterLiveData.observe(viewLifecycleOwner) {
            binding.counterTV.text = it.toString()
        }
    }

    private fun sendConnectionState(isAvailable: Boolean) {
        SavedValues.connectionLiveData.postValue(isAvailable)
    }

    private fun initBottomBar() {
        val navController =
            (childFragmentManager.findFragmentById(R.id.fragmentMainNavHostContainer) as NavHostFragment)
                .navController

        binding.bottomNavigationView.setupWithNavController(navController)
    }
}
