package com.klmn.slapp.ui.list.items

import android.animation.ArgbEvaluator
import android.animation.ValueAnimator
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.util.AttributeSet
import android.view.ActionMode
import android.view.LayoutInflater
import android.view.View
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.activity.OnBackPressedCallback
import androidx.activity.addCallback
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.animation.doOnEnd
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.transition.TransitionManager
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.transition.MaterialContainerTransform
import com.google.android.material.transition.MaterialElevationScale
import com.klmn.slapp.R
import com.klmn.slapp.common.MultiSelectListAdapter
import com.klmn.slapp.common.scrollToBottom
import com.klmn.slapp.databinding.TabItemsBinding
import com.klmn.slapp.domain.SlappItem
import com.klmn.slapp.ui.list.ListViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlin.math.abs

@ExperimentalCoroutinesApi
@AndroidEntryPoint
class ItemsTab : Fragment(), MultiSelectListAdapter.Callback<SlappItem> {
    private var _binding: TabItemsBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ListViewModel by activityViewModels()

    private var selectionToolbar: ActionMode? = null
    private lateinit var sheetBehavior: BottomSheetBehavior<View>
    private var collapseSheetCallback: OnBackPressedCallback? = null

    private var scrollOnSubmitList = true

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = TabItemsBinding.inflate(inflater, container, false)

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

        binding.newItemView.apply {
            itemText.apply {
                doAfterTextChanged(::updateFAB)
                setOnEditorActionListener { _, actionId, _ ->
                    if (actionId == EditorInfo.IME_ACTION_DONE) addNewItem()
                    true
                }
            }
            updateFAB(itemText.text)
        }

        sheetBehavior = BottomSheetBehavior.from(binding.bottomSheet.root)
        sheetBehavior.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onSlide(bottomSheet: View, slideOffset: Float) = Unit
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                if (newState == BottomSheetBehavior.STATE_HIDDEN) collapseSheetCallback?.remove()
            }
        })
        sheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN

        return binding.root
    }

    private fun updateFAB(text: Editable?) {
        if (text.isNullOrBlank() && viewModel.fabMode.value != ListViewModel.FABMode.SHOP) {
            fabAnimatorShop.start()
            viewModel.fabMode.value = ListViewModel.FABMode.SHOP
            binding.newItemView.addButton.setOnClickListener { enterShoppingMode() }
        }
        else if (viewModel.fabMode.value != ListViewModel.FABMode.ADD_ITEM) {
            fabAnimatorAdd.start()
            viewModel.fabMode.value = ListViewModel.FABMode.ADD_ITEM
            binding.newItemView.addButton.setOnClickListener { addNewItem() }
        }
    }

    private fun addNewItem() {
        if (binding.newItemView.itemText.text.isNullOrBlank()) return

        viewModel.addItem(binding.newItemView.itemText.text.toString())
        binding.newItemView.itemText.text.clear()
        scrollOnSubmitList = true
    }

    private fun enterShoppingMode() {
        sheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
        collapseSheetCallback = requireActivity().onBackPressedDispatcher.addCallback {
            sheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
            collapseSheetCallback = null
            remove()
        }
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

    class EnterItemBehavior(context: Context, attrs: AttributeSet) :
        CoordinatorLayout.Behavior<View>(context, attrs) {
        override fun layoutDependsOn(
            parent: CoordinatorLayout,
            child: View,
            dependency: View
        ) = dependency.id == R.id.bottom_sheet

        override fun onDependentViewChanged(
            parent: CoordinatorLayout,
            child: View,
            dependency: View
        ) = with(child) {
            val fraction = (dependency.height - dependency.top).toFloat() /
                    BottomSheetBehavior.from(dependency).peekHeight
            alpha = 1f - fraction
            scaleX = 1f - (fraction / 5f)
            scaleY = scaleX
            true
        }
    }

    private val fabAnimatorShop by lazy {
        with(binding.newItemView.addButton) {
            ValueAnimator.ofObject(
                ArgbEvaluator(),
                resources.getColor(R.color.secondaryColor),
                resources.getColor(R.color.primaryTextColor)
            ).apply {
                duration = 300L
                addUpdateListener {
                    backgroundTintList = ColorStateList.valueOf(it.animatedValue as Int)
                    scaleX = 1.1f - (abs(it.animatedFraction - .5f) / 5)
                    scaleY = scaleX
                    rotation = 360 * it.animatedFraction
                }
                doOnEnd { setImageResource(R.drawable.ic_shopping_cart) }
            }
        }
    }
    private val fabAnimatorAdd by lazy {
        with(binding.newItemView.addButton) {
            ValueAnimator.ofObject(
                ArgbEvaluator(),
                resources.getColor(R.color.primaryTextColor),
                resources.getColor(R.color.secondaryColor)
            ).apply {
                duration = 300L
                addUpdateListener {
                    backgroundTintList = ColorStateList.valueOf(it.animatedValue as Int)
                    scaleX = 1.1f - (abs(it.animatedFraction - .5f) / 5)
                    scaleY = scaleX
                    rotation = 360 * it.animatedFraction
                }
                doOnEnd { setImageResource(R.drawable.ic_add) }
            }
        }
    }
}