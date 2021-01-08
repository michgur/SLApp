package com.klmn.slapp.ui.list.items

import android.os.Bundle
import android.view.ActionMode
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.asLiveData
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.klmn.slapp.common.MultiSelectListAdapter
import com.klmn.slapp.common.scrollToBottom
import com.klmn.slapp.databinding.TabItemsBinding
import com.klmn.slapp.domain.SlappItem
import com.klmn.slapp.ui.list.ListViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect

@ExperimentalCoroutinesApi
@AndroidEntryPoint
class ItemsTab : Fragment(), MultiSelectListAdapter.Callback<SlappItem> {
    private var _binding: TabItemsBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ListViewModel by activityViewModels()

    private var selectionToolbar: ActionMode? = null

    private var scrollOnSubmitList = true

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = TabItemsBinding.inflate(inflater, container, false)

        val adapter = SlappListAdapter(viewModel.users.asLiveData(), viewModel.selection)
        adapter.addSelectionListener(this)

        viewModel.selectionModeEnabled.observe(viewLifecycleOwner) {
            selectionToolbar =
                if (it) requireActivity()
                    .startActionMode(SelectionModeCallback(requireContext(), viewModel, adapter))
                else {
                    selectionToolbar?.finish()
                    null
                }
        }

        viewModel.selectionModeTitle.observe(viewLifecycleOwner) {
            selectionToolbar?.title = it
        }

        binding.itemsRecyclerView.apply {
            lifecycleScope.launchWhenStarted {
                viewModel.items.collect {
                    adapter.submitList(it) {
                        if (scrollOnSubmitList) {
                            scrollOnSubmitList = false
                            scrollToBottom()
                        }
                    }
                }
            }

            this.adapter = adapter
            layoutManager = LinearLayoutManager(requireContext())
        }

        binding.newItemView.apply {
            itemText.apply {
                doAfterTextChanged { addButton.isEnabled = !it.isNullOrBlank() }
                setOnEditorActionListener { _, actionId, _ ->
                    if (actionId == EditorInfo.IME_ACTION_DONE) addNewItem()
                    true
                }
            }
            addButton.setOnClickListener { addNewItem() }
        }

        return binding.root
    }

    private fun addNewItem() {
        if (binding.newItemView.itemText.text.isNullOrBlank()) return

        viewModel.addItem(binding.newItemView.itemText.text.toString())
        binding.newItemView.itemText.text.clear()
        scrollOnSubmitList = true
    }

    override fun onSelectionStart() { viewModel.selectionModeEnabled.value = true }
    override fun onSelectionEnd() { viewModel.selectionModeEnabled.value = false }
    override fun onItemStateChanged(item: SlappItem, selected: Boolean) {
        viewModel.selection.apply {
            if (selected) add(item) else remove(item)
            viewModel.selectionModeTitle.value = "$size"
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}