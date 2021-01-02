package com.klmn.slapp.ui

import android.view.GestureDetector
import android.view.MotionEvent
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView

abstract class MultiSelectListAdapter<T, VH : RecyclerView.ViewHolder>(
    diffCallback: DiffUtil.ItemCallback<T>,
    /* When initializing adapter after configuration change, pass the previous selection.
    * Items will be selected after a list is first submitted to this adapter */
    private var initialSelection: Iterable<T>? = null
) : ListAdapter<T, VH>(diffCallback), RecyclerView.OnItemTouchListener {
    private val _selection = mutableSetOf<T>()
    val selection: Set<T> get() = _selection

    private val callbacks = mutableSetOf<Callback<T>>()

    fun isSelected(item: T) = _selection.contains(item)

    fun select(item: T) = select(currentList.indexOf(item))
    fun select(items: Iterable<T>): Unit = items.forEach(::select)
    fun selectAll() = currentList.indices.forEach(::select)

    fun deselect(item: T) = deselect(currentList.indexOf(item))
    fun deselect(items: Iterable<T>): Unit = items.forEach(::deselect)
    fun clearSelection() = currentList.indices.forEach(::deselect)

    fun addSelectionListener(listener: Callback<T>) = callbacks.add(listener)
    fun removeSelectionListener(listener: Callback<T>) = callbacks.remove(listener)

    override fun onCurrentListChanged(previousList: MutableList<T>, currentList: MutableList<T>) {
        val removed = _selection.retainAll(currentList::contains)
        // if the selection was cleared, call onSelectionEnd()
        if (removed && _selection.isEmpty()) callbacks.forEach(Callback<T>::onSelectionEnd)
        // if this is the first submitted list, use the initial selection
        if (previousList.isEmpty() && initialSelection != null) {
            initialSelection?.filter(currentList::contains)?.forEach(::select)
            initialSelection = null
        }
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        initGestureDetector(recyclerView)
        recyclerView.addOnItemTouchListener(this@MultiSelectListAdapter)
    }

    private fun select(position: Int) {
        val item = getItem(position)
        if (_selection.isEmpty()) callbacks.forEach(Callback<T>::onSelectionStart)
        if (_selection.add(item)) {
            notifyItemChanged(position, Unit)
            callbacks.forEach { it.onItemStateChanged(item, true) }
        }
    }
    private fun deselect(position: Int) {
        val item = getItem(position)
        if (_selection.remove(item)) {
            notifyItemChanged(position, Unit)
            callbacks.forEach { it.onItemStateChanged(item, false) }
            if (_selection.isEmpty()) callbacks.forEach(Callback<T>::onSelectionEnd)
        }
    }

    interface Callback<T> {
        fun onSelectionStart() {}
        fun onSelectionEnd() {}
        fun onItemStateChanged(item: T, selected: Boolean) {}
    }

    private lateinit var gestureDetector: GestureDetector
    private fun initGestureDetector(recyclerView: RecyclerView) {
        val listener = object : GestureDetector.SimpleOnGestureListener() {
            override fun onSingleTapUp(e: MotionEvent?) = true
            override fun onLongPress(e: MotionEvent) = with(recyclerView) {
                if (_selection.isEmpty())
                    findChildViewUnder(e.x, e.y)?.let { getChildViewHolder(it).adapterPosition }?.let(::select)
            }
        }
        gestureDetector = GestureDetector(recyclerView.context, listener)
    }

    override fun onInterceptTouchEvent(rv: RecyclerView, e: MotionEvent): Boolean {
        if (gestureDetector.onTouchEvent(e) && _selection.isNotEmpty()) {
            val vh = rv.findChildViewUnder(e.x, e.y)?.let { rv.getChildViewHolder(it).adapterPosition }!!
            if (_selection.contains(getItem(vh))) deselect(vh)
            else select(vh)
        }
        return false
    }

    override fun onTouchEvent(rv: RecyclerView, e: MotionEvent) = Unit
    override fun onRequestDisallowInterceptTouchEvent(disallowIntercept: Boolean) = Unit
}