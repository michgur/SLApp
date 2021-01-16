package com.klmn.slapp.ui.components

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.view.isVisible
import com.google.android.material.bottomsheet.BottomSheetBehavior

/* animates the view fading as the hideable bottomSheet is showing */
class HideUnderBottomSheetBehavior(context: Context, attrs: AttributeSet) :
    CoordinatorLayout.Behavior<View>(context, attrs) {
    override fun layoutDependsOn(
        parent: CoordinatorLayout,
        child: View,
        dependency: View
    ) = (dependency.layoutParams as? CoordinatorLayout.LayoutParams)?.behavior is BottomSheetBehavior

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
        // when fully transparent- set as GONE
        isVisible = alpha > 0f
        true
    }
}