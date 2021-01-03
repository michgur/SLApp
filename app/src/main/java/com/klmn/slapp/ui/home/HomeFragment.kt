package com.klmn.slapp.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.klmn.slapp.R
import com.klmn.slapp.databinding.FragmentHomeBinding
import com.klmn.slapp.domain.SlappList

class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: ListsAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)

        adapter = ListsAdapter(this)
        binding.listViewPager.adapter = adapter
        binding.listViewPager.setOnClickListener {
            findNavController().navigate(
                R.id.action_homeFragment_to_listFragment,
                bundleOf("listId" to 290L)
            )
        }

        return binding.root
    }

    private class ListsAdapter(home: Fragment) : FragmentStateAdapter(home) {
        private val lists = mutableListOf<SlappList>()

        override fun getItemCount() = lists.size
        override fun getItemId(position: Int) = lists[position].id
        override fun containsItem(itemId: Long) = lists.find { it.id == itemId } != null
        override fun createFragment(position: Int) = ListPreviewFragment(lists[position])
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}