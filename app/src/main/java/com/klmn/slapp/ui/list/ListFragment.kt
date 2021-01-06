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
import androidx.lifecycle.asLiveData
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.navigation.ui.setupWithNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.transition.MaterialSharedAxis
import com.klmn.slapp.common.MultiSelectListAdapter
import com.klmn.slapp.databinding.FragmentListBinding
import com.klmn.slapp.domain.SlappItem
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect

// next on the agenda- implement the rest of the UI:
//      HomeFragment that contains lists & list operations (requires fixing the db)
//      item & user operations in listView
//      check possibility of linking to some existing product dataSet on the web
@ExperimentalCoroutinesApi
@AndroidEntryPoint
class ListFragment : Fragment(), MultiSelectListAdapter.Callback<SlappItem> {
    private var _binding: FragmentListBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ListViewModel by viewModels()
    private val args: ListFragmentArgs by navArgs()

    private lateinit var adapter: SlappListAdapter
    private var selectionToolbar: ActionMode? = null

    private var scrollOnSubmitList = true

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentListBinding.inflate(inflater, container, false)

        viewModel.listId.value = args.listId

        enterTransition = MaterialSharedAxis(MaterialSharedAxis.Z, true).setDuration(500L)
        returnTransition = MaterialSharedAxis(MaterialSharedAxis.Z, false).setDuration(500L)

        binding.toolbar.apply {
            viewModel.listName.asLiveData().observe(viewLifecycleOwner, ::setTitle)
            setupWithNavController(findNavController())
            (requireActivity() as AppCompatActivity).setSupportActionBar(this)
        }

        viewModel.selectionModeEnabled.observe(viewLifecycleOwner) {
            if (it) {
                selectionToolbar?.finish()
                selectionToolbar = requireActivity()
                    .startActionMode(SelectionModeCallback(requireContext(), viewModel, adapter))
            }
        }

        adapter = SlappListAdapter(viewModel.selection.value)
        adapter.addSelectionListener(this)

        binding.itemsRecyclerView.apply {
            lifecycleScope.launchWhenStarted {
                viewModel.items.collect {
                    (adapter as SlappListAdapter).submitList(it) {
                        if (scrollOnSubmitList) {
                            scrollOnSubmitList = false
                            (layoutManager as LinearLayoutManager).scrollToPositionWithOffset(
                                adapter?.itemCount?.minus(1) ?: 0, 0)
                        }
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
        scrollOnSubmitList = true
    }

    override fun onSelectionStart() { viewModel.selectionModeEnabled.value = true }
    override fun onSelectionEnd() { viewModel.selectionModeEnabled.value = false }
    override fun onItemStateChanged(item: SlappItem, selected: Boolean) {
        viewModel.selection.value?.apply {
            if (selected) add(item) else remove(item)
            selectionToolbar?.title = size.toString()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}