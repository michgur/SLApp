package com.klmn.slapp.ui.home

import android.view.View
import android.view.View.INVISIBLE
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import com.klmn.slapp.common.BoundViewHolder
import com.klmn.slapp.common.scrollToBottom
import com.klmn.slapp.databinding.ViewItemSmallBinding
import com.klmn.slapp.databinding.ViewListSmallBinding
import com.klmn.slapp.domain.SlappItem
import com.klmn.slapp.domain.SlappItemDiff
import com.klmn.slapp.domain.SlappList
import com.klmn.slapp.domain.SlappListDiff

class ListPreviewAdapter(private val home: Fragment) :
    ListAdapter<SlappList, ListPreviewAdapter.ViewHolder>(SlappListDiff) {

    private var onItemClickListener: ((View) -> Unit)? = null
    fun setOnItemClickListener(listener: (View) -> Unit) { onItemClickListener = listener }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ViewHolder(parent).apply {
        binding.apply {
            itemsRecyclerView.apply {
                adapter = MiniItemAdapter()
                layoutManager = LinearLayoutManager(home.requireContext())
            }
            button.setOnClickListener {
                onItemClickListener?.invoke(root)
            }
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.binding.run {
        val list = currentList[position]
        toolbar.title = list.name
        itemsRecyclerView.run {
            (adapter as MiniItemAdapter).submitList(list.items, ::scrollToBottom)
        }
    }

    fun getListId(position: Int) = currentList[position].id
    fun getListName(position: Int) = currentList[position].name

    class ViewHolder(parent: ViewGroup) : BoundViewHolder<ViewListSmallBinding>(parent, ViewListSmallBinding::inflate)

    private class MiniItemAdapter : ListAdapter<SlappItem, MiniItemAdapter.ViewHolder>(SlappItemDiff) {
        class ViewHolder(parent: ViewGroup) : BoundViewHolder<ViewItemSmallBinding>(parent, ViewItemSmallBinding::inflate)

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ViewHolder(parent)
        override fun onBindViewHolder(holder: ViewHolder, position: Int) = with(holder.binding) {
            deleteBtn.visibility = INVISIBLE
            textName.text = getItem(position).name
        }
    }
}
