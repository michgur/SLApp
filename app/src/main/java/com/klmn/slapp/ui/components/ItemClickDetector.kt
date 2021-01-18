package com.klmn.slapp.ui.components

import android.view.GestureDetector
import android.view.MotionEvent
import androidx.recyclerview.widget.RecyclerView

/* a recyclerView touchListener that detects clicks & long clicks over items
* and notifies listeners */
class ItemClickDetector(
    private val recyclerView: RecyclerView
) : RecyclerView.OnItemTouchListener, GestureDetector.SimpleOnGestureListener() {
    private val gestureDetector by lazy {
        GestureDetector(recyclerView.context, this)
    }

    private var listeners = mutableListOf<Listener>()

    fun addListener(listener: Listener) = listeners.add(listener)
    fun removeListener(listener: Listener) = listeners.remove(listener)

    override fun onSingleTapUp(e: MotionEvent?) = true
    override fun onLongPress(e: MotionEvent) {
        recyclerView.run {
            findChildViewUnder(e.x, e.y)?.let {
                getChildViewHolder(it).adapterPosition
            }?.let { position ->
                listeners.forEach { it.onItemLongClick(position) }
            }
        }
    }

    override fun onInterceptTouchEvent(rv: RecyclerView, e: MotionEvent): Boolean {
        if (gestureDetector.onTouchEvent(e))
            rv.findChildViewUnder(e.x, e.y)?.let {
                rv.getChildViewHolder(it).adapterPosition
            }?.let { position ->
                listeners.forEach { it.onItemClick(position) }
            }
        return false
    }

    override fun onTouchEvent(rv: RecyclerView, e: MotionEvent) = Unit
    override fun onRequestDisallowInterceptTouchEvent(disallowIntercept: Boolean) = Unit

    /* implement this interface to act on item clicks */
    interface Listener {
        fun onItemClick(position: Int)
        fun onItemLongClick(position: Int) = Unit
    }
}