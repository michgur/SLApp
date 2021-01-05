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
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.transition.MaterialSharedAxis
import com.klmn.slapp.R
import com.klmn.slapp.databinding.FragmentHomeBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlin.math.abs

@ExperimentalCoroutinesApi
@AndroidEntryPoint
class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val viewModel: HomeViewModel by viewModels()

    private lateinit var adapter: ListPreviewAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)

        exitTransition = MaterialSharedAxis(MaterialSharedAxis.Z, true).setDuration(500L)
        reenterTransition = MaterialSharedAxis(MaterialSharedAxis.Z, false).setDuration(500L)

        adapter = ListPreviewAdapter(this)
        binding.listViewPager.apply {
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

        binding.fabAdd.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_createListFragment)
        }

        lifecycleScope.launchWhenStarted {
            viewModel.listsFlow.collect { it.forEach(adapter::addList) }
        }

        return binding.root
    }

    private class HorizontalMarginItemDecoration(context: Context, @DimenRes marginInDp: Int)
        : RecyclerView.ItemDecoration() {
        private val margin = context.resources.getDimension(marginInDp).toInt()
        override fun getItemOffsets(
            outRect: Rect,
            view: View,
            parent: RecyclerView,
            state: RecyclerView.State
        ) {
            outRect.right = margin
            outRect.left = margin
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}