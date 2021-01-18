package com.klmn.slapp.ui.components

import android.content.Context
import android.graphics.Rect
import android.view.View
import androidx.annotation.DimenRes
import androidx.recyclerview.widget.RecyclerView

/* applies horizontal margin to viewPager items */
class HorizontalMarginItemDecoration(context: Context, @DimenRes marginInDp: Int)
    : RecyclerView.ItemDecoration() {
    private val margin = context.resources.getDimension(marginInDp).toInt()
    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) = outRect.run {
        right = margin
        left = margin
    }
}