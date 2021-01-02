package com.klmn.slapp.ui

import android.view.LayoutInflater
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.klmn.slapp.R
import com.klmn.slapp.common.formatTimeStamp
import com.klmn.slapp.databinding.ViewItemBinding
import com.klmn.slapp.domain.SlappItem

class SlappListAdapter(selection: Iterable<SlappItem>? = null) :
    MultiSelectListAdapter<SlappItem, SlappListAdapter.ViewHolder>(SlappItemDiff, selection) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ViewHolder(parent)

    private object SlappItemDiff : DiffUtil.ItemCallback<SlappItem>() {
        override fun areContentsTheSame(oldItem: SlappItem, newItem: SlappItem) = oldItem == newItem
        override fun areItemsTheSame(oldItem: SlappItem, newItem: SlappItem) = oldItem.timestamp == newItem.timestamp
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position), isSelected(getItem(position)))
    }

    inner class ViewHolder(parent: ViewGroup) : RecyclerView.ViewHolder(
        LayoutInflater.from(parent.context).inflate(R.layout.view_item, parent, false)
    ) {
        var binding = ViewItemBinding.bind(itemView)

        fun bind(item: SlappItem, selected: Boolean) {
            binding.root.dispatchSetActivated(selected)

            if (adapterPosition > 0) binding.apply {
                textName.text = item.name
                textUser.text = item.user
                textTime.text = formatTimeStamp(item.timestamp)

                if (item.user == getItem(adapterPosition - 1).user)
                    textUser.visibility = GONE  // hide the user label if the above item is by the same user
                else divider.visibility = VISIBLE // otherwise show the divider
            }
        }
    }
}
