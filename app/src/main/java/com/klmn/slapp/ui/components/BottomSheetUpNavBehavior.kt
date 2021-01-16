package com.klmn.slapp.ui.components

import android.content.Context
import android.os.Parcelable
import android.util.AttributeSet
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.activity.OnBackPressedDispatcher
import androidx.activity.addCallback
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleOwner
import com.google.android.material.bottomsheet.BottomSheetBehavior

/* a bottom sheet behavior that also manages up navigation callbacks for hiding the bottom sheet */
class BottomSheetUpNavBehavior(context: Context, attrs: AttributeSet) :
    BottomSheetBehavior<View>(context, attrs) {
    // dispatcher & lifecycleOwner for making back press callbacks
    private var onBackPressedDispatcher: OnBackPressedDispatcher? = null
    private var lifecycleOwner: LifecycleOwner? = null
    // the current backPressedCallback if exists
    private var collapseCallback: OnBackPressedCallback? = null
    // whether should add a callback after configuration change
    private var addCallbackOnAttach = false

    init {
        addBottomSheetCallback(object : BottomSheetCallback() {
            override fun onSlide(bottomSheet: View, slideOffset: Float) = Unit
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                if (newState == STATE_HIDDEN) collapseCallback?.remove()
                else if (newState == STATE_COLLAPSED) addCallback()
            }
        })

        // start as hidden
        state = STATE_HIDDEN
    }

    fun hide() { state = STATE_HIDDEN }
    fun show() { state = STATE_COLLAPSED }
    fun expand() { state = STATE_EXPANDED }

    private fun addCallback() {
        if (collapseCallback != null) return
        collapseCallback = onBackPressedDispatcher?.addCallback(lifecycleOwner) {
            collapseCallback = null
            state = STATE_HIDDEN
            remove()
        }
    }

    companion object {
        fun from(fragment: Fragment, view: View) = (from(view) as BottomSheetUpNavBehavior).apply {
            onBackPressedDispatcher = fragment.requireActivity().onBackPressedDispatcher
            lifecycleOwner = fragment.viewLifecycleOwner
            if (addCallbackOnAttach) addCallback()
        }
    }

    override fun onRestoreInstanceState(parent: CoordinatorLayout, child: View, state: Parcelable) {
        super.onRestoreInstanceState(parent, child, state)
        if (this.state != STATE_HIDDEN) {
            onBackPressedDispatcher?.let { addCallback() }
            addCallbackOnAttach = true
        }
    }
}