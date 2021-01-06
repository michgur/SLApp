package com.klmn.slapp.domain

import androidx.recyclerview.widget.DiffUtil

object SlappListDiff : DiffUtil.ItemCallback<SlappList>() {
    override fun areItemsTheSame(oldItem: SlappList, newItem: SlappList) =
        oldItem.id == newItem.id

    override fun areContentsTheSame(oldItem: SlappList, newItem: SlappList) =
        oldItem == newItem
}

object SlappItemDiff : DiffUtil.ItemCallback<SlappItem>() {
    override fun areContentsTheSame(oldItem: SlappItem, newItem: SlappItem) = oldItem == newItem
    override fun areItemsTheSame(oldItem: SlappItem, newItem: SlappItem) = oldItem.timestamp == newItem.timestamp
}
