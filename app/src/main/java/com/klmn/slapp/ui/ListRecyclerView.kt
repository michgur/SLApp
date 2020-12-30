package com.klmn.slapp.ui

import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.recyclerview.selection.ItemDetailsLookup
import androidx.recyclerview.selection.ItemKeyProvider
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.klmn.slapp.R
import com.klmn.slapp.domain.SlappItem
import com.klmn.slapp.databinding.ViewItemBinding
import java.util.*

class SlappItemViewHolder(parent: ViewGroup) : RecyclerView.ViewHolder(
    LayoutInflater.from(parent.context).inflate(R.layout.view_item, parent, false)
) {
    private var binding = ViewItemBinding.bind(itemView)

    fun update(item: SlappItem, first: Boolean, hideUser: Boolean) = with(binding) {
        textName.text = item.name
        textUser.text = item.user
        textTime.text = Date(item.timestamp).toString().take(8)
        if (hideUser) textUser.visibility = GONE
        else if (!first) separator.visibility = VISIBLE
    }
}

private object SlappItemDiff : DiffUtil.ItemCallback<SlappItem>() {
    override fun areItemsTheSame(oldItem: SlappItem, newItem: SlappItem) =
        oldItem.timestamp == newItem.timestamp

    override fun areContentsTheSame(oldItem: SlappItem, newItem: SlappItem) =
        oldItem == newItem
}

//private class ItemIdKeyProvider(private val recyclerView: RecyclerView) :
//    ItemKeyProvider<Long>(SCOPE_MAPPED) {
//    override fun getKey(position: Int) = recyclerView.adapter?.getItemId(position)
//    override fun getPosition(key: Long) = recyclerView.findViewHolderForItemId(key).layoutPosition
//}
//
//private class ItemLookup(private val recyclerView: RecyclerView) : ItemDetailsLookup<Long> () {
//    override fun getItemDetails(e: MotionEvent): ItemDetails<Long>? {
//        val item = recyclerView.findChildViewUnder(e.x, e.y)
//        recyclerView.getChildViewHolder(item!!).getItemDe
//    }
//}

class SwipeToDelete(val adapter: SlappListAdapter) :
    ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {
    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean {
        TODO("Not yet implemented")
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        adapter.deleteItem(viewHolder.adapterPosition)
    }
}

class SlappListAdapter : ListAdapter<SlappItem, SlappItemViewHolder>(SlappItemDiff) {
    private var onDelete: ((SlappItem) -> Unit)? = null

    fun doOnDelete(action: (SlappItem) -> Unit) { onDelete = action }

    fun deleteItem(position: Int) = onDelete?.run {
        invoke(getItem(position))
        notifyItemRemoved(position)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        SlappItemViewHolder(parent)

    override fun onBindViewHolder(holder: SlappItemViewHolder, position: Int) {
        val item = getItem(position)
        holder.update(
            item, position == 0,
            position > 0 && item.user == getItem(position - 1).user)
    }
}
