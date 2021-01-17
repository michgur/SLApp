package com.klmn.slapp.ui.list.items

import android.animation.ValueAnimator
import android.view.ViewGroup
import androidx.core.animation.doOnEnd
import androidx.recyclerview.widget.ListAdapter
import com.klmn.slapp.common.BoundViewHolder
import com.klmn.slapp.databinding.ViewItemSmallBinding
import com.klmn.slapp.domain.SlappItem
import com.klmn.slapp.domain.SlappItemDiff

class ShoppingCartAdapter(private val onItemRemoved: (SlappItem) -> Unit) :
    ListAdapter<SlappItem, ShoppingCartAdapter.ViewHolder>(SlappItemDiff) {
    class ViewHolder(parent: ViewGroup) : BoundViewHolder<ViewItemSmallBinding>(parent, ViewItemSmallBinding::inflate)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ViewHolder(parent)

    override fun onBindViewHolder(holder: ViewHolder, position: Int) = with(holder.binding) {
        root.apply {
            scaleX = 1f
            scaleY = 1f
        }

        val item = getItem(position)
        textName.text = item.name
        deleteBtn.setOnClickListener {
            ValueAnimator.ofFloat(1f, 0f).apply{
                addUpdateListener {
                    root.scaleX = it.animatedValue as Float
                    root.scaleY = root.scaleX
                }
                doOnEnd { onItemRemoved(item) }
                duration = 500L
            }.start()
        }
    }
}