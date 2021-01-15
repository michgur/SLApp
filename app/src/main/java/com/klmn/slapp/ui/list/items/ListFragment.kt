package com.klmn.slapp.ui.list.items

import android.os.Bundle
import android.view.ActionMode
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.activity.OnBackPressedCallback
import androidx.activity.addCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.navigation.ui.setupWithNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.transition.MaterialSharedAxis
import com.klmn.slapp.R
import com.klmn.slapp.common.MultiSelectListAdapter
import com.klmn.slapp.common.scrollToBottom
import com.klmn.slapp.databinding.FragmentListItemsBinding
import com.klmn.slapp.domain.SlappItem
import com.klmn.slapp.ui.list.ListViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect

@ExperimentalCoroutinesApi
@AndroidEntryPoint
class ListFragment : Fragment(), MultiSelectListAdapter.Callback<SlappItem> {
    private var _binding: FragmentListItemsBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ListViewModel by activityViewModels()
    private val args: ListFragmentArgs by navArgs()

    private var selectionToolbar: ActionMode? = null
    private lateinit var sheetBehavior: BottomSheetBehavior<View>
    private var collapseSheetCallback: OnBackPressedCallback? = null

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
            findNavController().navigate(R.id.action_listFragment_to_listInfoFragment)
        }

        binding.toolbar.apply {
            setupWithNavController(findNavController())
            (requireActivity() as AppCompatActivity).setSupportActionBar(this)
        }

        viewModel.listName.observe(viewLifecycleOwner, binding.listName::setText)

        val adapter = SlappListAdapter(viewModel)
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

        // todo: shopping mode
        //      on item click -> hide item & put it in cart sheet
        //      checkout & cancel buttons
        //      navigate up -> hide sheet
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

        binding.newItemView.itemText.apply {
            doAfterTextChanged { viewModel.enterItemEnabled.value = !text.isNullOrBlank() }
            setOnEditorActionListener { _, actionId, _ ->
                if (actionId == EditorInfo.IME_ACTION_DONE) addNewItem()
                true
            }
            viewModel.enterItemEnabled.value = !text.isNullOrBlank()
        }
        viewModel.enterItemEnabled.observe(viewLifecycleOwner) {
            binding.newItemView.addButton.apply {
                if (it) {
                    addMode()
                    setOnClickListener { addNewItem() }
                } else {
                    shopMode()
                    setOnClickListener { enterShoppingMode() }
                }
            }
        }

        sheetBehavior = BottomSheetBehavior.from(binding.bottomSheet.root)
        // when user hides bottomSheet manually- remove the backNavigationCallback that collapses the sheet
        sheetBehavior.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onSlide(bottomSheet: View, slideOffset: Float) = Unit
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                if (newState == BottomSheetBehavior.STATE_HIDDEN) collapseSheetCallback?.remove()
            }
        })
        viewModel.bottomSheetState.observe(viewLifecycleOwner) {
            sheetBehavior.state = it
            if (it == BottomSheetBehavior.STATE_COLLAPSED && collapseSheetCallback == null)
                collapseSheetCallback = requireActivity().onBackPressedDispatcher.addCallback {
                    viewModel.bottomSheetState.value = BottomSheetBehavior.STATE_HIDDEN
                    collapseSheetCallback = null
                    remove()
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

    private fun enterShoppingMode() {
        viewModel.bottomSheetState.value = BottomSheetBehavior.STATE_COLLAPSED
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