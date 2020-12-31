package com.klmn.slapp.ui

import android.view.LayoutInflater
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import com.klmn.slapp.R
import com.klmn.slapp.common.SelectableItemDiff
import com.klmn.slapp.common.SelectableListAdapter
import com.klmn.slapp.common.SelectableViewHolder
import com.klmn.slapp.common.formatTimeStamp
import com.klmn.slapp.databinding.ViewItemBinding
import com.klmn.slapp.domain.SlappItem

class SlappItemViewHolder(parent: ViewGroup) : SelectableViewHolder<SlappItem>(
    LayoutInflater.from(parent.context).inflate(R.layout.view_item, parent, false)
) {
    var binding = ViewItemBinding.bind(itemView)

    override fun bind(item: SlappItem, selected: Boolean) = with(binding) {
        root.dispatchSetActivated(selected)

        textName.text = item.name
        textUser.text = item.user
        textTime.text = formatTimeStamp(item.timestamp)
    }
}

class SlappListAdapter :
    SelectableListAdapter<SlappItem, SlappItemViewHolder>("items", SlappItemDiff) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = SlappItemViewHolder(parent)

    override fun onBindViewHolder(holder: SlappItemViewHolder, position: Int) {
        super.onBindViewHolder(holder, position)

        if (position > 0) holder.binding.apply {
            if (getItem(position).user == getItem(position - 1).user)
                textUser.visibility = GONE  // hide the user label if the above item is by the same user
            else divider.visibility = VISIBLE // otherwise show the divider
        }
    }

    private object SlappItemDiff : SelectableItemDiff<SlappItem>() {
        override fun getId(item: SlappItem) = item.timestamp
        override fun areContentsTheSame(oldItem: SlappItem, newItem: SlappItem) = oldItem == newItem
    }
}
