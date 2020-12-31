package com.klmn.slapp.common

import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import androidx.lifecycle.LifecycleObserver
import androidx.recyclerview.selection.ItemDetailsLookup
import androidx.recyclerview.selection.SelectionTracker
import androidx.recyclerview.selection.StableIdKeyProvider
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

abstract class SelectableListAdapter<T, VH : SelectableViewHolder<T>>(
    private val name: String,
    private val diff: SelectableItemDiff<T>
) : ListAdapter<T, VH>(diff), LifecycleObserver {
    init { this.setHasStableIds(true) }
    private lateinit var tracker: SelectionTracker<Long>

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        @Suppress("UNCHECKED_CAST")
        val itemDetailsLookup = object : ItemDetailsLookup<Long>() {
            override fun getItemDetails(e: MotionEvent) = recyclerView.findChildViewUnder(e.x, e.y)
                ?.let { (recyclerView.getChildViewHolder(it) as VH).itemDetails }
        }
        tracker = SelectionTracker.Builder(
            "$name-selection",
            recyclerView,
            StableIdKeyProvider(recyclerView),
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
}