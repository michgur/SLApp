package com.klmn.slapp.ui.list

import android.os.Bundle
import android.view.ActionMode
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo.IME_ACTION_DONE
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.navigation.ui.setupWithNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.transition.MaterialSharedAxis
import com.klmn.slapp.common.MultiSelectListAdapter
import com.klmn.slapp.databinding.FragmentListBinding
import com.klmn.slapp.domain.SlappItem
import dagger.hilt.android.AndroidEntryPoint

// next on the agenda- implement the rest of the UI:
//      HomeFragment that contains lists & list operations (requires fixing the db)
//      item & user operations in listView
//      check possibility of linking to some existing product dataSet on the web
@AndroidEntryPoint
class ListFragment : Fragment(), MultiSelectListAdapter.Callback<SlappItem> {
    private val MODE_VIEW = 0
    private val MODE_SELECTION = 1

    private var _binding: FragmentListBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ListViewModel by viewModels()
    private val args: ListFragmentArgs by navArgs()

    private lateinit var adapter: SlappListAdapter
    private var selectionMode: ActionMode? = null

    private var addedItem = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentListBinding.inflate(inflater, container, false)

        viewModel.listId = args.listId

        enterTransition = MaterialSharedAxis(MaterialSharedAxis.Z, true).setDuration(500L)
        returnTransition = MaterialSharedAxis(MaterialSharedAxis.Z, false).setDuration(500L)

        binding.toolbar.apply {
            viewModel.listName.observe(viewLifecycleOwner, ::setTitle)
            setupWithNavController(findNavController())
            (requireActivity() as AppCompatActivity).setSupportActionBar(this)
        }

        viewModel.mode.observe(viewLifecycleOwner) {
            when (it) {
                MODE_VIEW -> selectionMode?.finish()
                MODE_SELECTION -> selectionMode =
                    requireActivity().startActionMode(SelectionModeCallback(requireContext(), viewModel, adapter))
            }
        }

        adapter = SlappListAdapter(viewModel.selection.value)
        adapter.addSelectionListener(this)

        binding.itemsRecyclerView.apply {
            viewModel.items.observe(viewLifecycleOwner) {
                (adapter as SlappListAdapter).submitList(it) {
                    if (addedItem) {
                        addedItem = false
                        (layoutManager as LinearLayoutManager).scrollToPositionWithOffset(
                            adapter?.itemCount?.minus(1) ?: 0, 0)
                    }
                }
            }

            adapter = this@ListFragment.adapter
            layoutManager = LinearLayoutManager(requireContext())
        }

        binding.newItemView.apply {
            itemText.apply {
                doAfterTextChanged { addButton.isEnabled = !it.isNullOrEmpty() }
                setOnEditorActionListener { _, actionId, _ ->
                    if (actionId == IME_ACTION_DONE) addNewItem()
                    true
                }
            }
            addButton.setOnClickListener { addNewItem() }
        }

        return binding.root
    }

    private fun addNewItem() {
        if (binding.newItemView.itemText.text.isNullOrEmpty()) return

        viewModel.addItem(binding.newItemView.itemText.text.toString())
        binding.newItemView.itemText.text.clear()
        addedItem = true
    }

    override fun onSelectionStart() { viewModel.mode.value = MODE_SELECTION }
    override fun onSelectionEnd() { viewModel.mode.value = MODE_VIEW }
    override fun onItemStateChanged(item: SlappItem, selected: Boolean) {
        viewModel.selection.value?.apply {
            if (selected) add(item) else remove(item)
            selectionMode?.title = size.toString()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}