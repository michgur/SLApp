package com.klmn.slapp.ui.list.items

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.coordinatorlayout.widget.CoordinatorLayout
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.klmn.slapp.R

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