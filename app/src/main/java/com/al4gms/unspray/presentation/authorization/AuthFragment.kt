package com.al4gms.unspray.presentation.authorization

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.transition.TransitionInflater
import com.al4gms.unspray.R
import com.al4gms.unspray.databinding.FragmentAuthorizationBinding
import com.al4gms.unspray.utils.ViewBindingFragment
import com.al4gms.unspray.utils.snackbar
import com.google.android.material.transition.MaterialContainerTransform
import dagger.hilt.android.AndroidEntryPoint
import net.openid.appauth.AuthorizationResponse
import timber.log.Timber

@AndroidEntryPoint
class AuthFragment :
    ViewBindingFragment<FragmentAuthorizationBinding>(FragmentAuthorizationBinding::inflate) {

    private val viewModel: AuthViewModel by viewModels()
    private lateinit var resultLauncher: ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedElementEnterTransition = MaterialContainerTransform(requireContext(), true)
        val inflater = TransitionInflater.from(requireContext())
        enterTransition = inflater.inflateTransition(R.transition.slide_right)
        exitTransition = inflater.inflateTransition(R.transition.fade)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        resultLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult(),
        ) { result ->
            val data: Intent? = result.data
            if (result.resultCode == Activity.RESULT_OK && data != null) {
                val tokenExchangeRequest = AuthorizationResponse.fromIntent(data)
                    ?.createTokenExchangeRequest()
                if (tokenExchangeRequest != null) {
                    viewModel.onAuthCodeReceived(tokenExchangeRequest)
                }
            } else {
                Timber.d("result code = ${result.resultCode}")
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bindViewModel()
    }

    private fun bindViewModel() {
        binding.button.setOnClickListener { viewModel.openLoginPage() }
        viewModel.loadingLiveData.observe(viewLifecycleOwner, ::updateIsLoading)
        viewModel.openAuthPageLiveData.observe(viewLifecycleOwner, ::openAuthPage)
        viewModel.errorToastLiveData.observe(viewLifecycleOwner) {
            binding.button.visibility = View.VISIBLE
            snackbar(binding.root, it)
        }
        viewModel.authSuccessLiveData.observe(viewLifecycleOwner) {
            binding.progressCircular.visibility = View.VISIBLE
            binding.button.visibility = View.GONE
            findNavController().navigate(
                AuthFragmentDirections.actionAuthFragmentToMainFragment(),
            )
        }
    }

    private fun updateIsLoading(isLoading: Boolean) {
        if (isLoading) {
            binding.button.visibility = View.GONE
            binding.progressCircular.visibility = View.VISIBLE
        } else {
            binding.progressCircular.visibility = View.GONE
            binding.button.visibility = View.VISIBLE
        }
    }

    private fun openAuthPage(intent: Intent) {
        resultLauncher.launch(intent)
    }
}
