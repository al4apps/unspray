package com.al4gms.unspray.presentation.start

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.al4gms.unspray.databinding.FragmentOnboardingPageBinding
import com.al4gms.unspray.utils.ViewBindingFragment
import com.al4gms.unspray.utils.withArguments
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class OnBoardingPageFragment :
    ViewBindingFragment<FragmentOnboardingPageBinding>(FragmentOnboardingPageBinding::inflate) {

    private val viewModel: StartingViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        when (requireArguments().getInt(KEY_PAGE_NUMBER)) {
            PAGE_FIRST -> {
                showViews(
                    binding.camerasImageView,
                    binding.ellipseImageView,
                    binding.titleTextView,
                    null,
                )
            }
            PAGE_SECOND -> {
                showViews(
                    binding.cameras2ImageView,
                    binding.ellipse2ImageView,
                    binding.titleTextView,
                    null,
                )
            }
            PAGE_THIRD -> {
                showViews(
                    binding.cameras3ImageView,
                    binding.ellipse3ImageView,
                    binding.titleTextView,
                    binding.continueButton,
                )
            }
        }

        binding.continueButton.setOnClickListener {
            viewModel.editFirstLaunchFlag()
            findNavController().navigate(StartingFragmentDirections.actionStartingFragmentToAuthFragment())
        }
    }

    private fun showViews(
        camerasIV: ImageView,
        ellipseIV: ImageView,
        titleTV: TextView,
        button: Button?,
    ) {
        ellipseIV.visibility = View.VISIBLE
        camerasIV.visibility = View.VISIBLE
        ellipseIV.setImageResource(
            requireArguments().getInt(
                KEY_IMAGE_ELLIPSE,
            ),
        )
        camerasIV.setImageResource(
            requireArguments().getInt(
                KEY_IMAGE_CAMERAS,
            ),
        )
        titleTV.setText(requireArguments().getInt(KEY_TITLE))
        button?.visibility = View.VISIBLE
    }

    companion object {

        const val PAGE_FIRST = 1
        const val PAGE_SECOND = 2
        const val PAGE_THIRD = 3

        private const val KEY_TITLE = "title"
        private const val KEY_IMAGE_ELLIPSE = "image ellipse"
        private const val KEY_IMAGE_CAMERAS = "image cameras"
        private const val KEY_PAGE_NUMBER = "page number"

        fun newInstance(
            @StringRes titleRes: Int,
            @DrawableRes ellipseDrawableRes: Int,
            @DrawableRes camerasDrawableRes: Int,
            pageNumber: Int,
        ): OnBoardingPageFragment {
            return OnBoardingPageFragment().withArguments {
                putInt(KEY_TITLE, titleRes)
                putInt(KEY_IMAGE_ELLIPSE, ellipseDrawableRes)
                putInt(KEY_IMAGE_CAMERAS, camerasDrawableRes)
                putInt(KEY_PAGE_NUMBER, pageNumber)
            }
        }
    }
}
