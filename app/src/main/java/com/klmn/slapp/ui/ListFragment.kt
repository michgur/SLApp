package com.klmn.slapp.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo.IME_ACTION_DONE
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.klmn.slapp.databinding.FragmentListBinding
import dagger.hilt.android.AndroidEntryPoint

// next on the agenda:
//      properly implement item editing & removal
//      implement the rest of the UI-
//          HomeFragment that contains lists & list operations
//          item & user operations in listView
//          check possibility of linking to some existing product dataSet on the web
@AndroidEntryPoint
class ListFragment : Fragment() {
    private var _binding: FragmentListBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ListViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentListBinding.inflate(inflater, container, false)

        binding.itemsRecyclerView.apply {
            viewModel.items.observe(viewLifecycleOwner) {
                (adapter as SlappListAdapter).submitList(it)
            }

            val slAdapter = SlappListAdapter()
            adapter = slAdapter
            slAdapter.loadSelection(savedInstanceState)
            slAdapter.doOnSelectionStart { println("selection start!") }
            slAdapter.doOnSelectionEnd { println("selection end!") }
            slAdapter.doOnItemSelection { item, selected -> println("item ${item.name} was ${if(selected) "" else "de"}selected!") }
            layoutManager = LinearLayoutManager(requireContext())
        }

        binding.newItemView.apply {
            itemText.apply{
                doAfterTextChanged { addButton.isEnabled = !it.isNullOrEmpty() }
                setOnEditorActionListener { _, actionId, _ ->
                    if (actionId == IME_ACTION_DONE) addNewItem()
                    true
                }
            }
            addButton.setOnClickListener { addNewItem() }
        }

        viewModel.addList("family")

        binding.toolbar.setupWithNavController(findNavController())
        return binding.root
    }

    // ui plan:
    //      'add item' button at the bottom that expands to a search view (bottom sheet?)
    //      (animates the recyclerView app) that has existing items to speed up adding items
    //      can return to view the list by swiping down
    //      upon choosing an item it is removed from search view, added to list, and search bar clears
    //      maybe have the bar at the top
    private fun addNewItem() {
        if (binding.newItemView.itemText.text.isNullOrEmpty()) return

        viewModel.addItem(binding.newItemView.itemText.text.toString())

        binding.itemsRecyclerView.apply {
            (layoutManager as LinearLayoutManager).scrollToPositionWithOffset(
                adapter?.itemCount?.minus(1) ?: 0, 0)
        }
        binding.newItemView.itemText.text.clear()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        (binding.itemsRecyclerView.adapter as SlappListAdapter).saveSelection(outState)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}