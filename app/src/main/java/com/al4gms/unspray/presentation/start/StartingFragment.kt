package com.al4gms.unspray.presentation.start

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.Navigation
import androidx.transition.TransitionInflater
import androidx.viewpager2.widget.ViewPager2
import com.al4gms.unspray.R
import com.al4gms.unspray.data.SavedValues
import com.al4gms.unspray.data.modelsui.OnBoardingPage
import com.al4gms.unspray.databinding.FragmentStartingBinding
import com.al4gms.unspray.presentation.adapters.OnBoardingAdapter
import com.al4gms.unspray.utils.ViewBindingFragment
import com.google.android.material.transition.MaterialContainerTransform
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class StartingFragment :
    ViewBindingFragment<FragmentStartingBinding>(FragmentStartingBinding::inflate) {

    private lateinit var onBoardingAdapter: OnBoardingAdapter
    private val viewModel: StartingViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedElementEnterTransition = MaterialContainerTransform(requireContext(), true)
        val inflater = TransitionInflater.from(requireContext())
        exitTransition = inflater.inflateTransition(R.transition.slide_left)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bindViewModel()
    }

    private fun bindViewModel() {
        viewModel.checkFirstLaunchFlag()
        viewModel.isFirstLaunchLiveData.observe(viewLifecycleOwner) { isFirstLaunch ->
            if (isFirstLaunch) {
                viewModel.getOnBoardingPages()
                showPages(binding.onBoardingViewPager)
                SavedValues.isAppReadyToStart = true
            } else {
                viewModel.getTokenFromSharedPrefs()
            }
        }
        viewModel.hasTokenLiveData.observe(viewLifecycleOwner) { hasToken ->
            SavedValues.isAppReadyToStart = true
            navigate(hasToken)
        }
        viewModel.pagesLiveData.observe(viewLifecycleOwner) {
            setAdapter(it)
        }
    }

    private fun navigate(hasToken: Boolean) {
        val navController =
            Navigation.findNavController(requireActivity(), R.id.activityNavHostFragmentContainer)

        val mainGraph = navController.navInflater.inflate(R.navigation.nav_graph)

        mainGraph.setStartDestination(
            if (hasToken) {
                R.id.mainFragment
            } else {
                R.id.authFragment
            },
        )
        navController.graph = mainGraph
    }

    private fun setAdapter(pages: Array<OnBoardingPage>) {
        val viewPager = binding.onBoardingViewPager
        viewPager.visibility = View.VISIBLE
        onBoardingAdapter = OnBoardingAdapter(pages, this)
        viewPager.adapter = onBoardingAdapter
    }

    private fun showPages(viewPager: ViewPager2) {
        setPosition(viewModel.currentPosition)

        binding.nextPageButton.setOnClickListener {
            val position = viewPager.currentItem
            if (position == 2) return@setOnClickListener
            binding.onBoardingViewPager.currentItem = position + 1
        }
        binding.prevPageButton.setOnClickListener {
            val position = viewPager.currentItem
            if (position == 0) return@setOnClickListener
            binding.onBoardingViewPager.currentItem = position - 1
        }
        viewPager.registerOnPageChangeCallback(object :
            ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                viewModel.currentPosition = position
                setPosition(position)
            }
        })
    }

    private fun setPosition(position: Int) {
        when (position) {
            0 -> {
                binding.nextPageButton.visibility = View.VISIBLE
                binding.prevPageButton.visibility = View.GONE
            }
            1 -> {
                binding.nextPageButton.visibility = View.VISIBLE
                binding.prevPageButton.visibility = View.VISIBLE
            }
            2 -> {
                binding.prevPageButton.visibility = View.VISIBLE
                binding.nextPageButton.visibility = View.GONE
            }
        }
    }
}
