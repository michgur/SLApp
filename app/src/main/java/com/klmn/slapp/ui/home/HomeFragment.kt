package com.klmn.slapp.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.klmn.slapp.databinding.FragmentHomeBinding
import com.klmn.slapp.domain.SlappItem
import com.klmn.slapp.domain.SlappList
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect

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
        binding.listViewPager.adapter = adapter

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
            println("added $list")
            if (lists.add(list))
                notifyItemInserted(lists.size - 1)
        }

        override fun getItemCount() = lists.size
        override fun getItemId(position: Int) = lists[position].id
        override fun containsItem(itemId: Long) = lists.find { it.id == itemId } != null
        override fun createFragment(position: Int) = ListPreviewFragment().also {
            println("creating a fragment")
            it.arguments = bundleOf(
                "listName" to lists[position].name,
                "items" to lists[position].items.map(SlappItem::name)
            )
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}