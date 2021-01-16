package com.klmn.slapp.ui.components

import android.view.GestureDetector
import android.view.MotionEvent
import androidx.recyclerview.widget.RecyclerView

class ItemClickDetector(
    private val recyclerView: RecyclerView
) : RecyclerView.OnItemTouchListener, GestureDetector.SimpleOnGestureListener() {
    private val gestureDetector by lazy {
        GestureDetector(recyclerView.context, this)
    }

    private var listener: Listener = object : Listener {
        override fun onItemClick(position: Int) = Unit
    }

    fun setListener(listener: Listener) { this.listener = listener }

    override fun onSingleTapUp(e: MotionEvent?) = true
    override fun onLongPress(e: MotionEvent) {
        recyclerView.run {
            findChildViewUnder(e.x, e.y)?.let {
                getChildViewHolder(it).adapterPosition
            }?.let(listener::onItemLongClick)
        }
    }

    override fun onInterceptTouchEvent(rv: RecyclerView, e: MotionEvent): Boolean {
        if (gestureDetector.onTouchEvent(e))
            rv.findChildViewUnder(e.x, e.y)?.let {
                rv.getChildViewHolder(it).adapterPosition
            }?.let(listener::onItemClick)
        return false
    }

    override fun onTouchEvent(rv: RecyclerView, e: MotionEvent) = Unit
    override fun onRequestDisallowInterceptTouchEvent(disallowIntercept: Boolean) = Unit

    interface Listener {
        fun onItemClick(position: Int)
        fun onItemLongClick(position: Int) = Unit
    }
}