package com.klmn.slapp.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import com.klmn.slapp.R
import com.klmn.slapp.common.SelectableItemDiff
import com.klmn.slapp.common.SelectableListAdapter
import com.klmn.slapp.common.SelectableViewHolder
import com.klmn.slapp.databinding.ViewItemBinding
import com.klmn.slapp.domain.SlappItem
import java.util.*

class SlappItemViewHolder(parent: ViewGroup) : SelectableViewHolder<SlappItem>(
    LayoutInflater.from(parent.context).inflate(R.layout.view_item, parent, false)
) {
    var binding = ViewItemBinding.bind(itemView)

    override fun bind(item: SlappItem, selected: Boolean) = with(binding) {
        root.dispatchSetActivated(selected)

        textName.text = item.name
        textUser.text = item.user
        textTime.text = Date(item.timestamp).toString().take(8)

//        if (adapterPosition > 0) {
//            if (get) textUser.visibility = GONE fixme
//            else separator.visibility = VISIBLE
//        }
    }
}

class SlappListAdapter :
    SelectableListAdapter<SlappItem, SlappItemViewHolder>("items", SlappItemDiff) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = SlappItemViewHolder(parent)

    private object SlappItemDiff : SelectableItemDiff<SlappItem>() {
        override fun getId(item: SlappItem) = item.timestamp
        override fun areContentsTheSame(oldItem: SlappItem, newItem: SlappItem) = oldItem == newItem
    }
}
