package com.klmn.slapp.ui.list.items

import android.animation.ValueAnimator
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.core.animation.doOnEnd
import androidx.recyclerview.widget.RecyclerView
import com.klmn.slapp.common.BoundViewHolder
import com.klmn.slapp.ui.components.MultiSelectListAdapter
import com.klmn.slapp.common.formatTimeStamp
import com.klmn.slapp.databinding.ViewItemBinding
import com.klmn.slapp.domain.SlappItem
import com.klmn.slapp.domain.SlappItemDiff
import kotlinx.coroutines.ExperimentalCoroutinesApi

@ExperimentalCoroutinesApi
class SlappListAdapter(
    private val viewModel: ListItemsViewModel,
    private val onItemRemoved: (SlappItem) -> Unit
) : MultiSelectListAdapter<SlappItem, SlappListAdapter.ViewHolder>(SlappItemDiff, viewModel.selection) {
    private lateinit var recyclerView: RecyclerView

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ViewHolder(parent)

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position), isSelected(position))
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        this.recyclerView = recyclerView
    }

    override fun onItemClick(position: Int) {
        if (viewModel.shoppingModeEnabled.value != true) super.onItemClick(position)
        else {
            val view = recyclerView.findViewHolderForAdapterPosition(position)!!.itemView
            ValueAnimator.ofFloat(1f, 0f).apply{
                addUpdateListener {
                    view.scaleX = it.animatedValue as Float
                    view.scaleY = view.scaleX
                }
                doOnEnd {
                    onItemRemoved(getItem(position))
                    // that's garbage
                    submitList(mutableListOf<SlappItem>().apply{
                        addAll(currentList)
                        removeAt(position)
                    })
                }
                duration = 500L
            }.start()
        }
    }

    override fun onItemLongClick(position: Int) {
        if (viewModel.shoppingModeEnabled.value != true) super.onItemLongClick(position)
    }

    inner class ViewHolder(parent: ViewGroup) :
        BoundViewHolder<ViewItemBinding>(parent, ViewItemBinding::inflate) {
        fun bind(item: SlappItem, selected: Boolean) = binding.apply {
            root.apply {
                isActivated = selected
                scaleX = 1f
                scaleY = 1f
            }

            textName.text = item.name
            textTime.text = formatTimeStamp(item.timestamp)
            textUser.text = viewModel.users.value?.find {
                it.phoneNumber == item.user
            }?.displayName ?: item.user

            if (adapterPosition > 0)  {
                if (item.user == getItem(adapterPosition - 1).user)
                    textUser.visibility = GONE  // hide the user label if the above item is by the same user
                else divider.visibility = VISIBLE // otherwise show the divider
            }
        }
    }
}
