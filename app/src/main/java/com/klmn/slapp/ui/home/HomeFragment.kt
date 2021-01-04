package com.klmn.slapp.ui.home

import android.content.Context
import android.graphics.Rect
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.annotation.DimenRes
import androidx.cardview.widget.CardView
import androidx.core.os.bundleOf
import androidx.core.view.get
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.adapter.FragmentViewHolder
import com.klmn.slapp.R
import com.klmn.slapp.databinding.FragmentHomeBinding
import com.klmn.slapp.domain.SlappItem
import com.klmn.slapp.domain.SlappList
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.buffer
import kotlinx.coroutines.flow.collect
import kotlin.math.abs

@ExperimentalCoroutinesApi
@AndroidEntryPoint
class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val viewModel: HomeViewModel by viewModels()

    private lateinit var adapter: ListsAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)

        adapter = ListsAdapter(this)
        adapter.addList(
            SlappList(1, "michael", "", 0L, mutableListOf(
                SlappItem("poop"),
                SlappItem("item"),
                SlappItem("hello"),
            )))
        adapter.addList(
            SlappList(2, "friends", "", 0L, mutableListOf(
                SlappItem("tomato"),
                SlappItem("cucumber"),
                SlappItem("gamba"),
                SlappItem("onion"),
            )))
        adapter.addList(
            SlappList(3, "party", "", 0L, mutableListOf(
                SlappItem("plates"),
                SlappItem("cake"),
                SlappItem("whatever"),
            )))

        binding.listViewPager.apply {
            adapter = this@HomeFragment.adapter
            offscreenPageLimit = 1
            (get(0) as ViewGroup).clipChildren = false

            val peek = resources.getDimension(R.dimen.viewpager_peek)
            val hPadding = resources.getDimension(R.dimen.viewpager_hpadding)
            val translationX = -(peek + hPadding)
            setPageTransformer { page, position ->
                page as FrameLayout
                val card = page[0] as CardView
                card.cardElevation = 4 + (12 * (1 - abs(position)))
                page.translationX = translationX * position
                page.scaleY = 1 - (.025f * abs(position))
            }
            addItemDecoration(HorizontalMarginItemDecoration(context, R.dimen.viewpager_hpadding))
        }

        lifecycleScope.launchWhenStarted {
            viewModel.listsFlow.collect {
                when (it) {
                    is HomeViewModel.ListState.GotList -> adapter.addList(it.list)
                }
            }
        }

        return binding.root
    }

    private class ListsAdapter(home: Fragment) : FragmentStateAdapter(home) {
        private val lists = mutableListOf<SlappList>()

        fun addList(list: SlappList) {
            if (lists.add(list))
                notifyItemInserted(lists.size - 1)
        }

        override fun getItemCount() = lists.size
        override fun getItemId(position: Int) = lists[position].id
        override fun containsItem(itemId: Long) = lists.find { it.id == itemId } != null
        override fun createFragment(position: Int) = ListPreviewFragment().also {
            // next: format the lists to a smaller size & add peeking
            // should transform to a ListFragment on click
            // should also be highlighted somehow when changed by other users
            it.arguments = bundleOf(
                "listName" to lists[position].name,
                "items" to lists[position].items.map(SlappItem::name)
            )
        }
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