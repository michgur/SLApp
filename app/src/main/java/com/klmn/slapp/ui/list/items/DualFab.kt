package com.klmn.slapp.ui.list.items

import android.animation.ArgbEvaluator
import android.animation.ValueAnimator
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.os.Parcelable
import android.util.AttributeSet
import androidx.annotation.ColorInt
import androidx.core.animation.doOnEnd
import androidx.core.content.res.ResourcesCompat
import androidx.core.os.bundleOf
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.klmn.slapp.R
import kotlin.math.abs

/* a floating action button that has two modes and can animate between them */
class DualFab @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = R.attr.floatingActionButtonStyle
): FloatingActionButton(context, attrs, defStyleAttr) {
    private var _mode: Boolean // true = alt mode, false = default mode

    /* the default mode color / icon. make sure to use the app:backgroundTint attribute, NOT the android: one */
    @ColorInt private var defBgTint: Int
    private var defIcon: Drawable

    @ColorInt private var altBgTint: Int
    private var altIcon: Drawable

    private var defOnClickListener: OnClickListener? = null
    private var altOnClickListener: OnClickListener? = null

    override fun setOnClickListener(l: OnClickListener?) { defOnClickListener = l }
    fun setAltOnClickListener(l: OnClickListener) { altOnClickListener = l }

    /* set the mode to alt(true) / default(false). returns whether the mode has changed */
    fun mode(alt: Boolean, animate: Boolean = true) = (_mode xor alt).also {
        if (alt) altMode(animate)
        else defMode(animate)
    }

    /* animates to alt mode. returns whether the mode has changed */
    fun altMode(animate: Boolean = true) = (!_mode).also {
        if (!_mode && animate) {
            _mode = true
            altAnimator.start()
        } else {
            backgroundTintList = ColorStateList.valueOf(altBgTint)
            setImageDrawable(altIcon)
        }
    }

    /* animates to default mode. returns whether the mode has changed */
    fun defMode(animate: Boolean = true) = _mode.also {
        if (_mode && animate) {
            _mode = false
            defAnimator.start()
        } else {
            backgroundTintList = ColorStateList.valueOf(defBgTint)
            setImageDrawable(defIcon)
        }
    }

    init {
        context.theme.obtainStyledAttributes(attrs, R.styleable.DualFab, 0, 0).apply {
            try {
                _mode = getBoolean(R.styleable.DualFab_altDefault, false)
                altBgTint = getInteger(R.styleable.DualFab_altBackgroundTint, Color.BLACK)
                altIcon = ResourcesCompat.getDrawable(
                    resources,
                    getResourceId(R.styleable.DualFab_altIcon, android.R.drawable.ic_menu_revert),
                    context.theme
                )!!
            } finally {
                recycle()
            }
        }

        defBgTint = backgroundTintList?.defaultColor ?: Color.WHITE
        defIcon = drawable

        super.setOnClickListener {
            if (_mode) altOnClickListener?.onClick(it)
            else defOnClickListener?.onClick(it)
        }

        mode(_mode, false)
    }

    // savedInstanceState keys
    companion object {
        const val KEY_MODE = "dualfab.mode"
        const val KEY_SUPER = "dualfab.super"
    }

    override fun onSaveInstanceState() = bundleOf(
        KEY_MODE to _mode,
        KEY_SUPER to super.onSaveInstanceState()
    )
    override fun onRestoreInstanceState(state: Parcelable) = super.onRestoreInstanceState(
        if (state is Bundle) {
            _mode = state.getBoolean(KEY_MODE)
            state.getParcelable(KEY_SUPER)
        }
        else state
    ).also { mode(_mode, false) }

    private val onAnimatorUpdate = ValueAnimator.AnimatorUpdateListener {
        backgroundTintList = ColorStateList.valueOf(it.animatedValue as Int)
        scaleX = 1.1f - (abs(it.animatedFraction - .5f) / 5)
        scaleY = scaleX
        rotation = 360 * it.animatedFraction
    }

    private val defAnimator = ValueAnimator.ofObject(ArgbEvaluator(), altBgTint, defBgTint).apply {
        duration = 300L
        addUpdateListener(onAnimatorUpdate)
        doOnEnd { setImageDrawable(defIcon) }
    }
    private val altAnimator = ValueAnimator.ofObject(ArgbEvaluator(), defBgTint, altBgTint).apply {
        duration = 300L
        addUpdateListener(onAnimatorUpdate)
        doOnEnd { setImageDrawable(altIcon) }
    }
}