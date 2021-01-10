package com.klmn.slapp.ui.home

import android.view.LayoutInflater
import android.view.View
import android.view.View.INVISIBLE
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.klmn.slapp.R
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

    class ViewHolder(parent: ViewGroup) : RecyclerView.ViewHolder(
        LayoutInflater.from(parent.context).inflate(R.layout.view_list_small, parent, false)
    ) { val binding = ViewListSmallBinding.bind(itemView) }

    private class MiniItemAdapter : ListAdapter<SlappItem, MiniItemAdapter.ViewHolder>(SlappItemDiff) {
        class ViewHolder(val binding: ViewItemSmallBinding) : RecyclerView.ViewHolder(binding.root)

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ViewHolder(
            ViewItemSmallBinding.inflate(
                LayoutInflater.from(parent.context),
                parent, false)
        )

        override fun onBindViewHolder(holder: ViewHolder, position: Int) = with(holder.binding) {
            deleteBtn.visibility = INVISIBLE
            textName.text = getItem(position).name
        }
    }
}
