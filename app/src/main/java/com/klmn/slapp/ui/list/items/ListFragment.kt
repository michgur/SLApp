package com.klmn.slapp.ui.list.items

import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.view.ActionMode
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.navigation.ui.setupWithNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.transition.MaterialSharedAxis
import com.klmn.slapp.common.scrollToBottom
import com.klmn.slapp.databinding.FragmentListItemsBinding
import com.klmn.slapp.domain.SlappItem
import com.klmn.slapp.ui.components.BottomSheetUpNavBehavior
import com.klmn.slapp.ui.components.MultiSelectListAdapter
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
            viewModel.cartItems.value += it
        }
        adapter.addSelectionListener(this)

        viewModel.selectionModeEnabled.observe(viewLifecycleOwner) {
            selectionToolbar =
                if (it) (requireActivity() as AppCompatActivity)
                    .startSupportActionMode(SelectionModeCallback(requireContext(), viewModel, adapter))
                else {
                    selectionToolbar?.finish()
                    null
                }
        }

        viewModel.selectionModeTitle.observe(viewLifecycleOwner) {
            selectionToolbar?.title = it
        }

        // todo: consider using an MVI approach and doing more of the logic in the viewModel
        //      notifications
        //      fix the broken app start UX
        //      pretty much done
        binding.itemsRecyclerView.apply {
            this.adapter = adapter
            layoutManager = LinearLayoutManager(requireContext())
        }

        lifecycleScope.launchWhenStarted {
            viewModel.listItems.collect {
                adapter.submitList(it) {
                    if (scrollOnSubmitList) {
                        scrollOnSubmitList = false
                        binding.itemsRecyclerView.scrollToBottom()
                    }
                }
            }
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

        sheetBehavior = BottomSheetUpNavBehavior.from(this, binding.bottomSheet.root)
        sheetBehavior.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onSlide(bottomSheet: View, slideOffset: Float) = Unit
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                viewModel.shoppingModeEnabled.value = newState != BottomSheetBehavior.STATE_HIDDEN
            }
        })

        binding.bottomSheet.apply {
            sheetTop.setOnClickListener { sheetBehavior.expand() }
            btnDone.setOnClickListener { finishShoppingMode(true) }
            btnCancel.setOnClickListener { finishShoppingMode(false) }
            itemsRecyclerView.apply {
                this.adapter = ShoppingCartAdapter {
                    viewModel.cartItems.value -= it
                }
                layoutManager = LinearLayoutManager(requireContext())
            }

            lifecycleScope.launchWhenStarted {
                viewModel.cartItems.collect {
                    (itemsRecyclerView.adapter as ShoppingCartAdapter).submitList(it)
                    btnDone.isVisible = it.isNotEmpty()
                }
            }
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

    private fun finishShoppingMode(success: Boolean) {
        viewModel.finishShopping(success)
        sheetBehavior.hide()
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