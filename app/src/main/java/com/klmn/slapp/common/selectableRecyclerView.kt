package com.klmn.slapp.common

import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import androidx.lifecycle.LifecycleObserver
import androidx.recyclerview.selection.ItemDetailsLookup
import androidx.recyclerview.selection.ItemKeyProvider
import androidx.recyclerview.selection.SelectionTracker
import androidx.recyclerview.selection.StorageStrategy
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView

abstract class SelectableViewHolder<T>(itemView: View) : RecyclerView.ViewHolder(itemView) {
    abstract fun bind(item: T, selected: Boolean)

    val itemDetails = object : ItemDetailsLookup.ItemDetails<Long>() {
        override fun getPosition() = adapterPosition
        override fun getSelectionKey() = itemId
    }
}

abstract class SelectableItemDiff<T> : DiffUtil.ItemCallback<T>() {
    abstract fun getId(item: T): Long
    override fun areItemsTheSame(oldItem: T, newItem: T) = getId(oldItem) == getId(newItem)
}

/**
 * These classes help implement a recyclerview w/ multiple selection without much of the boilerplate code.
 * To implement a selectable recyclerview:
 *       - implement a SelectableViewHolder class. the type parameter is the item type.
 *           inside the bind() function, setup the itemView with the item parameter,
 *           and set it as de/selected according to the second parameter.
 *       - implement a SelectableItemDiff class/ object that generates a stable ID for a given item,
 *           and implements a deep compare for items.
 *       - implement a SelectableListAdapter class/ object. the constructor & type parameters are
 *           same to ListAdapter, but it takes an additional name string, that will be used as a
 *           unique selection id, and should stay the same between different instances of the class.
 *           SelectableListAdapter automatically binds selectableViewHolders by calling their bind() function.
 *       - for selection persistence, call saveSelection() inside your activity/ fragment onSaveInstanceState(),
 *           and call loadSelection() inside onCreate (must be called AFTER the adapter wad attached to the recyclerview)
 */
abstract class SelectableListAdapter<T, VH : SelectableViewHolder<T>>(
    private val name: String,
    private val diff: SelectableItemDiff<T>,
    savedInstanceState: Bundle? = null
) : ListAdapter<T, VH>(diff), LifecycleObserver {
    private lateinit var tracker: SelectionTracker<Long>

    private lateinit var positions: Map<Long, Int>
    private val keyProvider = object : ItemKeyProvider<Long>(SCOPE_MAPPED) {
        override fun getKey(position: Int) = getItemId(position)
        override fun getPosition(key: Long) = positions[key] ?: RecyclerView.NO_POSITION
    }

    private val onAttachedCallbacks = mutableListOf<() -> Unit>()

    init {
        this.setHasStableIds(true)
        loadSelection(savedInstanceState)
    }

    override fun onCurrentListChanged(previousList: MutableList<T>, currentList: MutableList<T>) {
        positions = currentList.withIndex().associate { diff.getId(it.value) to it.index }
        tracker.selection.retainAll(positions::contains)
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        @Suppress("UNCHECKED_CAST")
        val itemDetailsLookup = object : ItemDetailsLookup<Long>() {
            override fun getItemDetails(e: MotionEvent) = recyclerView.findChildViewUnder(e.x, e.y)
                ?.let { (recyclerView.getChildViewHolder(it) as VH).itemDetails }
        }

        tracker = SelectionTracker.Builder(
            "$name-selection",
            recyclerView,
            keyProvider,
            itemDetailsLookup,
            StorageStrategy.createLongStorage()
        ).build()

        onAttachedCallbacks.forEach { it() }
    }

    override fun onBindViewHolder(holder: VH, position: Int) = getItem(position).let {
        holder.bind(it, tracker.isSelected(diff.getId(it)))
    }

    override fun getItemId(position: Int) = diff.getId(getItem(position))

    fun saveSelection(outState: Bundle) = doOnAttached {
        tracker.onSaveInstanceState(outState)
    }

    fun loadSelection(savedInstanceState: Bundle?) = doOnAttached {
        tracker.onRestoreInstanceState(savedInstanceState)
    }

    fun clearSelection() = doOnAttached { tracker.clearSelection() }
    fun selectAll() = doOnAttached {
        tracker.setItemsSelected(currentList.map(diff::getId), true)
    }

    fun selectionSize(): Int {
        if (!::tracker.isInitialized)
            throw IllegalStateException("adapter is not attached to a recyclerview!")
        return tracker.selection.size()
    }

    fun selection(): List<T> {
        if (!::tracker.isInitialized)
            throw IllegalStateException("adapter is not attached to a recyclerview!")
        return tracker.selection.map { getItem(keyProvider.getPosition(it)) }
    }

    private fun doOnAttached(action: () -> Unit) {
        if (::tracker.isInitialized) action()
        else onAttachedCallbacks.add(action)
    }

    fun doOnItemSelection(action: (item: T, selected: Boolean) -> Unit) = doOnAttached {
        tracker.addObserver(object : SelectionTracker.SelectionObserver<Long>() {
            override fun onItemStateChanged(key: Long, selected: Boolean) =
                    action(getItem(keyProvider.getPosition(key)), selected)
        })
    }

    fun doOnSelectionEnd(action: () -> Unit) = doOnAttached {
        tracker.addObserver(object : SelectionTracker.SelectionObserver<Long>() {
            override fun onSelectionChanged() {
                if (selectionSize() == 0) action()
            }
        })
    }

    fun doOnSelectionStart(action: () -> Unit) = doOnAttached {
        tracker.addObserver(object : SelectionTracker.SelectionObserver<Long>() {
            override fun onItemStateChanged(key: Long, selected: Boolean) {
                if (selected && selectionSize() == 1) action()
            }
        })
    }
}