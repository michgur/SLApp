package com.klmn.slapp.ui

import android.view.LayoutInflater
import android.view.View.GONE
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
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

    fun update(item: SlappItem, hideUser: Boolean = false) = with(binding) {
        textName.text = item.name
        textUser.text = item.user
        textTime.text = Date(item.timestamp).toString().take(8)
        if (hideUser) textUser.visibility = GONE
    }
}

private object SlappItemDiff : DiffUtil.ItemCallback<SlappItem>() {
    override fun areItemsTheSame(oldItem: SlappItem, newItem: SlappItem) =
        oldItem.timestamp == newItem.timestamp

    override fun areContentsTheSame(oldItem: SlappItem, newItem: SlappItem) =
        oldItem == newItem
}

class SlappListAdapter : ListAdapter<SlappItem, SlappItemViewHolder>(SlappItemDiff) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        SlappItemViewHolder(parent)

    override fun onBindViewHolder(holder: SlappItemViewHolder, position: Int) {
        val item = getItem(position)
        holder.update(item, position > 0 && item.user == getItem(position - 1).user)
    }
}
