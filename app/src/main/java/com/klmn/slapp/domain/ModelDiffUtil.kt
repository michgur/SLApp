package com.klmn.slapp.domain

import androidx.recyclerview.widget.DiffUtil

object SlappListDiff : DiffUtil.ItemCallback<SlappList>() {
    override fun areItemsTheSame(oldItem: SlappList, newItem: SlappList) =
        oldItem.id == newItem.id

    override fun areContentsTheSame(oldItem: SlappList, newItem: SlappList) =
        oldItem == newItem
}

object SlappItemDiff : DiffUtil.ItemCallback<SlappItem>() {
    override fun areItemsTheSame(oldItem: SlappItem, newItem: SlappItem) =
        oldItem.timestamp == newItem.timestamp

    override fun areContentsTheSame(oldItem: SlappItem, newItem: SlappItem) =
        oldItem == newItem
}

object ContactDiff : DiffUtil.ItemCallback<Contact>() {
    override fun areItemsTheSame(oldItem: Contact, newItem: Contact) =
        oldItem.phoneNumber == newItem.phoneNumber

    override fun areContentsTheSame(oldItem: Contact, newItem: Contact) =
        oldItem == newItem
}
