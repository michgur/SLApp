package com.klmn.slapp.ui.home

import android.content.Context
import android.graphics.Rect
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.DimenRes
import androidx.cardview.widget.CardView
import androidx.core.view.get
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import androidx.transition.Transition
import com.google.android.material.transition.MaterialSharedAxis
import com.klmn.slapp.R
import com.klmn.slapp.databinding.FragmentHomeBinding
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
            .setDuration(500L).addTarget(binding.root)
        reenterTransition = MaterialSharedAxis(MaterialSharedAxis.Z, false)
            .setDuration(500L).addTarget(binding.root)

        adapter = ListPreviewAdapter(this)
        adapter.setOnItemClickListener(::onPreviewClick)

        lifecycleScope.launchWhenStarted {
            viewModel.listsFlow.collect {
                adapter.submitList(it) {
                    binding.viewPagerLists.setCurrentItem(viewModel.position, false)
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

    private fun onPreviewClick(preview: View) {
        (exitTransition as Transition).targets[0] = preview
        (reenterTransition as Transition).targets[0] = preview

        viewModel.position = binding.viewPagerLists.currentItem
        findNavController().navigate(
            HomeFragmentDirections.actionHomeFragmentToListFragment(
                adapter.getListId(viewModel.position),
                adapter.getListName(viewModel.position)
            )
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}