package com.klmn.slapp.common

import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import androidx.lifecycle.LifecycleObserver
import androidx.recyclerview.selection.*
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

/*
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
* */
abstract class SelectableListAdapter<T, VH : SelectableViewHolder<T>>(
    private val name: String,
    private val diff: SelectableItemDiff<T>
) : ListAdapter<T, VH>(diff), LifecycleObserver {
    init { this.setHasStableIds(true) }
    private lateinit var keyProvider: ItemKeyProvider<Long>
    private lateinit var tracker: SelectionTracker<Long>

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        @Suppress("UNCHECKED_CAST")
        val itemDetailsLookup = object : ItemDetailsLookup<Long>() {
            override fun getItemDetails(e: MotionEvent) = recyclerView.findChildViewUnder(e.x, e.y)
                ?.let { (recyclerView.getChildViewHolder(it) as VH).itemDetails }
        }
        keyProvider = StableIdKeyProvider(recyclerView)
        tracker = SelectionTracker.Builder(
            "$name-selection",
            recyclerView,
            keyProvider,
            itemDetailsLookup,
            StorageStrategy.createLongStorage()
        ).build()
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val item = getItem(position)
        holder.bind(item, tracker.isSelected(diff.getId(item)))
    }

    override fun getItemId(position: Int) = diff.getId(getItem(position))

    fun saveSelection(outState: Bundle) = tracker.onSaveInstanceState(outState)
    fun loadSelection(savedInstanceState: Bundle?) = tracker.onRestoreInstanceState(savedInstanceState)

    fun doOnItemSelection(action: (item: T, selected: Boolean) -> Unit) =
        tracker.addObserver(object : SelectionTracker.SelectionObserver<Long>() {
            override fun onItemStateChanged(key: Long, selected: Boolean) =
                action(getItem(keyProvider.getPosition(key)), selected)
        })

    fun doOnSelectionEnd(action: () -> Unit) {
        tracker.addObserver(object : SelectionTracker.SelectionObserver<Long>() {
            override fun onSelectionCleared() = action()
            override fun onSelectionChanged() {
                if (tracker.selection.isEmpty) action()
            }
        })
    }

    fun doOnSelectionStart(action: () -> Unit) {
        tracker.addObserver(object : SelectionTracker.SelectionObserver<Long>() {
            override fun onItemStateChanged(key: Long, selected: Boolean) {
                if (selected && tracker.selection.size() == 1) action()
            }
        })
    }
}