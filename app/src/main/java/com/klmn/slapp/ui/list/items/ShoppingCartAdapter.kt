package com.klmn.slapp.ui.list.items

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.klmn.slapp.common.BoundViewHolder
import com.klmn.slapp.databinding.ViewItemSmallBinding
import com.klmn.slapp.domain.SlappItem

class ShoppingCartAdapter : RecyclerView.Adapter<ShoppingCartAdapter.ViewHolder>() {
    class ViewHolder(parent: ViewGroup) : BoundViewHolder<ViewItemSmallBinding>(parent, ViewItemSmallBinding::inflate)

    private val items = mutableListOf<SlappItem>()

    fun addItem(item: SlappItem) {
        items.add(item)
        notifyItemInserted(items.size - 1)
    }

    fun addItems(items: List<SlappItem>) {
        this.items.addAll(items)
        notifyItemRangeInserted(this.items.size - items.size, items.size)
    }

    override fun getItemCount() = items.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ViewHolder(parent)

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.textName.text = items[position].name
    }
}