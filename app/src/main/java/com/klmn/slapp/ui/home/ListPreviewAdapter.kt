package com.klmn.slapp.ui.home

import android.view.View
import android.view.View.INVISIBLE
import android.view.ViewGroup
import androidx.lifecycle.LiveData
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import com.klmn.slapp.R
import com.klmn.slapp.common.BoundViewHolder
import com.klmn.slapp.common.scrollToBottom
import com.klmn.slapp.databinding.ViewItemSmallBinding
import com.klmn.slapp.databinding.ViewListSmallBinding
import com.klmn.slapp.domain.SlappItem
import com.klmn.slapp.domain.SlappItemDiff
import com.klmn.slapp.domain.SlappList
import com.klmn.slapp.domain.SlappListDiff

class ListPreviewAdapter(private val favorites: LiveData<Set<String>>) :
    ListAdapter<SlappList, ListPreviewAdapter.ViewHolder>(SlappListDiff) {

    private var onItemClickListener: ((View) -> Unit)? = null
    fun setOnItemClickListener(listener: (View) -> Unit) { onItemClickListener = listener }

    private var onItemFavorite: ((SlappList) -> Unit)? = null
    fun setOnItemFavorite(listener: (SlappList) -> Unit) { onItemFavorite = listener }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ViewHolder(parent).apply {
        binding.apply {
            recyclerViewItems.apply {
                adapter = MiniItemAdapter()
                layoutManager = LinearLayoutManager(parent.context)
            }
            btnOverlay.setOnClickListener {
                onItemClickListener?.invoke(root)
            }
            btnFavorite.setOnClickListener {
                onItemFavorite?.invoke(currentList[adapterPosition])
                favorite = !favorite
            }
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.binding.run {
        val list = currentList[position]
        toolbar.title = list.name
        recyclerViewItems.run {
            (adapter as MiniItemAdapter).submitList(list.items, ::scrollToBottom)
        }
        holder.favorite = favorites.value?.contains(list.id) == true
    }

    fun getListId(position: Int) = currentList[position].id
    fun getListName(position: Int) = currentList[position].name

    fun getListPosition(id: String) = currentList.indexOfFirst { it.id == id }

    class ViewHolder(parent: ViewGroup) : BoundViewHolder<ViewListSmallBinding>(parent, ViewListSmallBinding::inflate) {
        var favorite = false
            set(value) {
                field = value
                binding.btnFavorite.setImageResource(
                    if (value) R.drawable.ic_star_filled
                    else R.drawable.ic_star_empty
                )
            }
    }

    private class MiniItemAdapter : ListAdapter<SlappItem, MiniItemAdapter.ViewHolder>(SlappItemDiff) {
        class ViewHolder(parent: ViewGroup) : BoundViewHolder<ViewItemSmallBinding>(parent, ViewItemSmallBinding::inflate)

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ViewHolder(parent)
        override fun onBindViewHolder(holder: ViewHolder, position: Int) = with(holder.binding) {
            btnClear.visibility = INVISIBLE
            textName.text = getItem(position).name
        }
    }
}
