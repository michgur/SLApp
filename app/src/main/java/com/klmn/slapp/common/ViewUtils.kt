package com.klmn.slapp.common

import android.app.Activity
import android.content.Context
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

// assumes layoutManager is a LinearLayoutManager
fun RecyclerView.scrollToBottom() {
    (layoutManager as LinearLayoutManager).scrollToPositionWithOffset(
        adapter?.itemCount?.minus(1) ?: 0, 0)
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
