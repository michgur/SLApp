package com.klmn.slapp

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.klmn.slapp.databinding.ViewItemBinding

class ListItemViewHolder(parent: ViewGroup) : RecyclerView.ViewHolder(
    LayoutInflater.from(parent.context).inflate(R.layout.view_item, parent, false)
) {
    private var binding = ViewItemBinding.bind(itemView)
}