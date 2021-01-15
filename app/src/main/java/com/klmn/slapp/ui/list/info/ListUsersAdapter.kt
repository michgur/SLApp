package com.klmn.slapp.ui.list.info

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.klmn.slapp.R
import com.klmn.slapp.databinding.ViewUserBinding
import com.klmn.slapp.domain.Contact
import com.klmn.slapp.domain.ContactDiff

class ListUsersAdapter(private val onAddUsersClick: View.OnClickListener) :
    ListAdapter<Contact, ListUsersAdapter.ViewHolder>(ContactDiff) {
    // first item in the list is the add users button
    override fun getItemCount() = super.getItemCount() + 1
    override fun getItemViewType(position: Int) = if (position == 0) 1 else 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ViewHolder(
            LayoutInflater.from(parent.context).inflate(
            if (viewType == 0) R.layout.view_user else R.layout.view_add_users,
            parent,
            false
        ))

    override fun onBindViewHolder(holder: ViewHolder, position: Int) =
        if (holder.itemViewType == 0) ViewUserBinding.bind(holder.itemView).run {
            val contact = getItem(position - 1)
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
        } else holder.itemView.setOnClickListener(onAddUsersClick)

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
}