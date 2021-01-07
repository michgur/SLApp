package com.klmn.slapp.ui.list

import android.view.LayoutInflater
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.lifecycle.LiveData
import androidx.recyclerview.widget.RecyclerView
import com.klmn.slapp.R
import com.klmn.slapp.common.MultiSelectListAdapter
import com.klmn.slapp.common.formatTimeStamp
import com.klmn.slapp.databinding.ViewItemBinding
import com.klmn.slapp.domain.Contact
import com.klmn.slapp.domain.SlappItem
import com.klmn.slapp.domain.SlappItemDiff

class SlappListAdapter(
    private val users: LiveData<List<Contact>>,
    selection: Iterable<SlappItem>? = null
) : MultiSelectListAdapter<SlappItem, SlappListAdapter.ViewHolder>(SlappItemDiff, selection) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ViewHolder(parent)

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position), isSelected(getItem(position)))
    }

    inner class ViewHolder(parent: ViewGroup) : RecyclerView.ViewHolder(
        LayoutInflater.from(parent.context).inflate(R.layout.view_item, parent, false)
    ) {
        var binding = ViewItemBinding.bind(itemView)

        fun bind(item: SlappItem, selected: Boolean) = binding.apply {
            root.isActivated = selected

            textName.text = item.name
            textUser.text = users.value?.find { it.phoneNumber == item.user }?.displayName ?: item.user
            textTime.text = formatTimeStamp(item.timestamp)

            if (adapterPosition > 0)  {
                if (item.user == getItem(adapterPosition - 1).user)
                    textUser.visibility = GONE  // hide the user label if the above item is by the same user
                else divider.visibility = VISIBLE // otherwise show the divider
            }
        }
    }
}
