package com.klmn.slapp.ui.list.items

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.activity.OnBackPressedCallback
import androidx.activity.addCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.view.ActionMode
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.asLiveData
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.navigation.ui.setupWithNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.transition.MaterialSharedAxis
import com.klmn.slapp.R
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
    private lateinit var exitDialogCallback: OnBackPressedCallback

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

        binding.viewTitle.setOnClickListener {
            findNavController().navigate(
                ListFragmentDirections.actionListFragmentToListInfoFragment(
                    args.listId,
                    viewModel.listName.value ?: ""
                )
            )
        }

        binding.toolbar.apply {
            setupWithNavController(findNavController())
            (requireActivity() as AppCompatActivity).setSupportActionBar(this)
        }

        viewModel.listName.observe(viewLifecycleOwner, binding.textListName::setText)

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

        binding.recyclerViewItems.apply {
            this.adapter = adapter
            layoutManager = LinearLayoutManager(requireContext())
        }

        lifecycleScope.launchWhenStarted {
            viewModel.listItems.collect {
                adapter.submitList(it) {
                    if (scrollOnSubmitList) {
                        scrollOnSubmitList = false
                        binding.recyclerViewItems.scrollToBottom()
                    }
                }
            }
        }

        binding.viewItemInput.apply {
            textItem.apply {
                doAfterTextChanged { dualFab.mode(text.isNullOrBlank()) }
                setOnEditorActionListener { _, actionId, _ ->
                    if (actionId == EditorInfo.IME_ACTION_DONE) addNewItem()
                    true
                }
            }
            dualFab.apply {
                setOnClickListener { addNewItem() }
                setAltOnClickListener { enterShoppingMode() }
            }
        }

        sheetBehavior = BottomSheetUpNavBehavior.from(this, binding.bottomSheetShop.root)
        sheetBehavior.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onSlide(bottomSheet: View, slideOffset: Float) = Unit
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                viewModel.shoppingModeEnabled.value = newState != BottomSheetBehavior.STATE_HIDDEN
            }
        })

        binding.bottomSheetShop.apply {
            viewTitle.setOnClickListener {
                sheetBehavior.apply {
                    if (isExpanded()) collapse()
                    else expand()
                }
            }
            btnDone.setOnClickListener { finishShoppingMode(true) }
            btnCancel.setOnClickListener { finishShoppingMode(false) }
            recyclerViewItems.apply {
                this.adapter = ShoppingCartAdapter {
                    viewModel.cartItems.value -= it
                }
                layoutManager = LinearLayoutManager(requireContext())
            }

            lifecycleScope.launchWhenStarted {
                viewModel.cartItems.collect {
                    (recyclerViewItems.adapter as ShoppingCartAdapter).submitList(it)
                    btnDone.isVisible = it.isNotEmpty()
                }
            }
        }

        exitDialogCallback = requireActivity().onBackPressedDispatcher
            .addCallback(viewLifecycleOwner) {
                findNavController().navigate(R.id.action_listFragment_to_exitDialogFragment)
            }
        // if user still has items in the cart, show exit dialog
        viewModel.cartItems.asLiveData().observe(viewLifecycleOwner) {
            exitDialogCallback.isEnabled = it.isNotEmpty()

            binding.viewItemInput.apply {
                if (it.isNotEmpty() && root.isVisible && dualFab.isAltMode())
                    dualFab.animate()
                        .scaleX(1.2f)
                        .scaleY(1.2f)
                        .rotation(-25f)
                        .setDuration(150L).withEndAction {
                            dualFab.animate()
                                .scaleX(1.0f)
                                .scaleY(1.0f)
                                .rotation(0f)
                                .setDuration(150L)
                                .start()
                        }.start()
            }
        }

        return binding.root
    }

    private fun addNewItem() {
        if (binding.viewItemInput.textItem.text.isNullOrBlank()) return

        viewModel.addItem(binding.viewItemInput.textItem.text.toString().trim())
        binding.viewItemInput.textItem.text.clear()
        scrollOnSubmitList = true
    }

    private fun enterShoppingMode() {
        if (viewModel.cartItems.value.isEmpty()) sheetBehavior.collapse()
        else sheetBehavior.expand()
    }

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