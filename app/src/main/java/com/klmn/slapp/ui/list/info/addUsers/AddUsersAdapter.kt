package com.klmn.slapp.ui.list.info.addUsers

import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import com.klmn.slapp.common.BoundViewHolder
import com.klmn.slapp.common.MultiSelectListAdapter
import com.klmn.slapp.databinding.ViewUserBinding
import com.klmn.slapp.domain.Contact
import com.klmn.slapp.domain.ContactDiff

class AddUsersAdapter(selection: Set<Contact>? = null) :
    MultiSelectListAdapter<Contact, AddUsersAdapter.ViewHolder>(ContactDiff, selection) {
    class ViewHolder(parent: ViewGroup) : BoundViewHolder<ViewUserBinding>(parent, ViewUserBinding::inflate)

    override fun onItemLongClick(position: Int) = Unit
    override fun onItemClick(position: Int) {
        if (isSelected(position)) deselect(position)
        else select(position)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ViewHolder(parent)

    override fun onBindViewHolder(holder: ViewHolder, position: Int) = with(holder.binding) {
        val contact = getItem(position)
        if (contact.displayName == null) {
            textIcon.text = "?"
            textUser.text = contact.phoneNumber
            textNumber.visibility = View.GONE
        } else {
            textIcon.text = contact.displayName
            textUser.text = contact.displayName
            textNumber.visibility = View.VISIBLE
            textNumber.text = contact.phoneNumber
        }
        selected.isVisible = isSelected(position)
    }
}