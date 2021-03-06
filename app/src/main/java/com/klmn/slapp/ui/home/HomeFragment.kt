package com.klmn.slapp.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.cardview.widget.CardView
import androidx.core.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.transition.Transition
import com.google.android.material.transition.MaterialSharedAxis
import com.klmn.slapp.R
import com.klmn.slapp.databinding.FragmentHomeBinding
import com.klmn.slapp.domain.SlappList
import com.klmn.slapp.ui.MainActivity
import com.klmn.slapp.ui.components.HorizontalMarginItemDecoration
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlin.math.abs

@ExperimentalCoroutinesApi
@AndroidEntryPoint
class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val viewModel: HomeViewModel by activityViewModels()

    private lateinit var adapter: ListPreviewAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)

        exitTransition = MaterialSharedAxis(MaterialSharedAxis.Z, true)
            .setDuration(500L)
        reenterTransition = MaterialSharedAxis(MaterialSharedAxis.Z, false)
            .setDuration(500L)

        adapter = ListPreviewAdapter(viewModel.favorites)
        adapter.setOnItemClickListener(::onPreviewClick)
        adapter.setOnItemFavorite {
            // maintain position since lists will be reordered
            viewModel.viewedListId = it.id
            viewModel.smoothScroll = true
            viewModel.flipFavorite(it.id)
        }

        lifecycleScope.launchWhenStarted {
            viewModel.listsFlow.collect { lists ->
                binding.progressBar.isVisible = false
                adapter.submitList(lists) {
                    adapter.getListPosition(viewModel.viewedListId).takeIf { it >= 0 }?.let { id ->
                        // the viewPager scroll behavior is odd, but this seems to mostly fix it
                        binding.viewPagerLists.apply {
                            if (viewModel.smoothScroll) doOnLayout { setCurrentItem(id, true) }
                            else setCurrentItem(id, false)
                        }
                    }
                }
            }
        }

        binding.fabAdd.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_createListFragment)
        }

        binding.btnRequestPermission.apply {
            viewModel.hidePermissionRequest.observe(viewLifecycleOwner) { isVisible = !it }
            setOnClickListener {
                (requireActivity() as MainActivity).requestReadContactsPermission()
            }
        }

        binding.viewPagerLists.apply {
            adapter = this@HomeFragment.adapter
            offscreenPageLimit = 1
            (get(0) as ViewGroup).clipChildren = false

            val peek = resources.getDimension(R.dimen.viewpager_peek)
            val hPadding = resources.getDimension(R.dimen.viewpager_hpadding)
            val translationX = -(peek + hPadding)
            setPageTransformer { page, position ->
                (page as CardView).cardElevation = 4 + (12 * (1 - abs(position)))
                page.translationX = translationX * position
                page.scaleY = 1 - (.025f * abs(position))
            }
            addItemDecoration(HorizontalMarginItemDecoration(context, R.dimen.viewpager_hpadding))
        }

        return binding.root
    }

    private fun onPreviewClick(list: SlappList, preview: View) {
        (exitTransition as Transition).targets.apply {
            clear()
            add(preview)
        }
        (reenterTransition as Transition).targets.apply {
            clear()
            add(preview)
        }

        viewModel.viewedListId = list.id
        viewModel.smoothScroll = false
        findNavController().navigate(
            HomeFragmentDirections.actionHomeFragmentToListFragment(
                viewModel.viewedListId,
                list.name
            )
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}