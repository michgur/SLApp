package com.klmn.slapp.common

import android.app.Activity
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding

/* the only way to scroll to bottom that works properly.
* this assumes layoutManager is an instance of LinearLayoutManager */
fun RecyclerView.scrollToBottom() {
    (layoutManager as? LinearLayoutManager)?.scrollToPositionWithOffset(
        adapter?.itemCount?.minus(1) ?: 0, 0)
}

open class BoundViewHolder<V : ViewBinding>(val binding: V) :
    RecyclerView.ViewHolder(binding.root) {
    constructor(parent: ViewGroup, inflater: (LayoutInflater, ViewGroup, Boolean) -> V) :
            this(inflater(LayoutInflater.from(parent.context), parent, false))
}

fun Activity.hideKeyboard() {
    val inputManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    currentFocus?.let { focus ->
        inputManager.hideSoftInputFromWindow(
            focus.windowToken,
            InputMethodManager.HIDE_NOT_ALWAYS
        )
    }
}

fun Fragment.hideKeyboard() { requireActivity().hideKeyboard() }

fun Fragment.getColor(@ColorRes id: Int) =
    ResourcesCompat.getColor(resources, id, requireContext().theme)

fun View.getColor(@ColorRes id: Int) =
    ResourcesCompat.getColor(resources, id, context.theme)

fun Fragment.getDrawable(@DrawableRes id: Int) =
    ResourcesCompat.getDrawable(resources, id, requireContext().theme)

fun View.getDrawable(@DrawableRes id: Int) =
    ResourcesCompat.getDrawable(resources, id, context.theme)
