package com.klmn.slapp.ui.list.items

import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import com.klmn.slapp.common.BoundViewHolder
import com.klmn.slapp.databinding.ViewItemSmallBinding
import com.klmn.slapp.domain.SlappItem
import com.klmn.slapp.domain.SlappItemDiff

class ShoppingCartAdapter : ListAdapter<SlappItem, ShoppingCartAdapter.ViewHolder>(SlappItemDiff) {
    class ViewHolder(parent: ViewGroup) : BoundViewHolder<ViewItemSmallBinding>(parent, ViewItemSmallBinding::inflate)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ViewHolder(parent)

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.textName.text = currentList[position].name
    }
}