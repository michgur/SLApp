package com.klmn.slapp.ui.list.items

import android.os.Bundle
import android.view.ActionMode
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.navigation.ui.setupWithNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.transition.MaterialSharedAxis
import com.klmn.slapp.ui.components.MultiSelectListAdapter
import com.klmn.slapp.common.scrollToBottom
import com.klmn.slapp.databinding.FragmentListItemsBinding
import com.klmn.slapp.domain.SlappItem
import com.klmn.slapp.ui.components.BottomSheetUpNavBehavior
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect

@ExperimentalCoroutinesApi
@AndroidEntryPoint
class ListFragment : Fragment(), MultiSelectListAdapter.Callback<SlappItem> {
    private var _binding: FragmentListItemsBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ListItemsViewModel by viewModels()
    private val args: ListFragmentArgs by navArgs()

    private var selectionToolbar: ActionMode? = null
    private lateinit var sheetBehavior: BottomSheetUpNavBehavior

    private var scrollOnSubmitList = true

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentListItemsBinding.inflate(inflater, container, false)

        viewModel.listId.value = args.listId

        enterTransition = MaterialSharedAxis(MaterialSharedAxis.Z, true).setDuration(500L)
        returnTransition = MaterialSharedAxis(MaterialSharedAxis.Z, false).setDuration(500L)

        binding.titleBox.setOnClickListener {
            findNavController().navigate(
                ListFragmentDirections.actionListFragmentToListInfoFragment(args.listId)
            )
        }

        binding.toolbar.apply {
            setupWithNavController(findNavController())
            (requireActivity() as AppCompatActivity).setSupportActionBar(this)
        }

        viewModel.listName.observe(viewLifecycleOwner, binding.listName::setText)

        val adapter = SlappListAdapter(viewModel) {
            viewModel.shoppingCart.add(it)
            (binding.bottomSheet.itemsRecyclerView.adapter as ShoppingCartAdapter).addItem(it)
        }
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

        // todo: clean this class
        //      separate different components to different classes
        //      consider using an MVI approach and doing more of the logic in the viewModel
        binding.itemsRecyclerView.apply {
            lifecycleScope.launchWhenStarted {
                viewModel.items.collect {
                    val list = mutableListOf<SlappItem>().apply {
                        addAll(it)
                        removeAll(viewModel.shoppingCart)
                    }
                    adapter.submitList(list) {
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

        binding.newItemView.itemText.apply {
            doAfterTextChanged { binding.newItemView.addButton.mode(text.isNullOrBlank()) }
            setOnEditorActionListener { _, actionId, _ ->
                if (actionId == EditorInfo.IME_ACTION_DONE) addNewItem()
                true
            }
        }
        binding.newItemView.addButton.apply {
            setOnClickListener { addNewItem() }
            setAltOnClickListener { enterShoppingMode() }
        }

        viewModel.shoppingModeEnabled.observe(viewLifecycleOwner) {
            binding.newItemView.root.isVisible = !it
        }

        sheetBehavior = BottomSheetUpNavBehavior.from(this, binding.bottomSheet.root)

        binding.bottomSheet.sheetTop.setOnClickListener {
            sheetBehavior.expand()
        }
        binding.bottomSheet.itemsRecyclerView.apply {
            this.adapter = ShoppingCartAdapter().apply { addItems(viewModel.shoppingCart) }
            layoutManager = LinearLayoutManager(requireContext())
        }

        return binding.root
    }

    private fun addNewItem() {
        if (binding.newItemView.itemText.text.isNullOrBlank()) return

        viewModel.addItem(binding.newItemView.itemText.text.toString())
        binding.newItemView.itemText.text.clear()
        scrollOnSubmitList = true
    }

    private fun enterShoppingMode() = sheetBehavior.show()

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