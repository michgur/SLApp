package com.klmn.slapp.ui.list.items

import android.animation.ArgbEvaluator
import android.animation.ValueAnimator
import android.content.Context
import android.content.res.ColorStateList
import android.util.AttributeSet
import androidx.core.animation.doOnEnd
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.klmn.slapp.R
import kotlin.math.abs

class ListFAB @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = R.attr.floatingActionButtonStyle
): FloatingActionButton(context, attrs, defStyleAttr) {
    fun animateShopIcon() = shopAnimator.start()
    fun animateAddIcon() = addAnimator.start()

    private val shopAnimator = ValueAnimator.ofObject(
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

    private val addAnimator = ValueAnimator.ofObject(
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