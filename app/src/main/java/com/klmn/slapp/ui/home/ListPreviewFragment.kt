package com.klmn.slapp.ui.home

import android.animation.AnimatorInflater
import android.animation.ValueAnimator
import android.view.LayoutInflater
import android.view.View
import android.view.View.INVISIBLE
import android.view.ViewGroup
import androidx.core.animation.doOnEnd
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.klmn.slapp.R
import com.klmn.slapp.databinding.ViewItemSmallBinding
import com.klmn.slapp.databinding.ViewListSmallBinding
import com.klmn.slapp.domain.SlappItem
import com.klmn.slapp.domain.SlappList
import kotlinx.coroutines.ExperimentalCoroutinesApi

@ExperimentalCoroutinesApi
class ListPreviewAdapter(private val home: Fragment) :
    RecyclerView.Adapter<ListPreviewAdapter.ViewHolder>() {
    private val lists = mutableListOf<SlappList>()

    fun addList(list: SlappList) {
        lists.add(list)
        notifyItemInserted(lists.size - 1)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ViewHolder(parent)

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        ViewListSmallBinding.bind(holder.itemView).apply {
            toolbar.title = lists[position].name
            itemsRecyclerView.apply {
                adapter = MiniItemAdapter().apply {
                    submitList(lists[position].items.map(SlappItem::name))
                }
                layoutManager = LinearLayoutManager(home.requireContext())
            }
            button.setOnClickListener { navigateWithZoomTransition(root) }
        }
    }

    private fun navigateWithZoomTransition(view: View) =
        AnimatorInflater.loadAnimator(home.requireContext(), R.animator.preview_scale).apply {
            setTarget(view)
            doOnEnd { home.findNavController().navigate(R.id.action_homeFragment_to_listFragment) }
        }.start()

    override fun getItemCount() = lists.size

    class ViewHolder(parent: ViewGroup) : RecyclerView.ViewHolder(
        LayoutInflater.from(parent.context).inflate(R.layout.view_list_small, parent, false)
    )

    private class MiniItemAdapter : ListAdapter<String, MiniItemAdapter.ViewHolder>(SlappItemDiff) {
        class ViewHolder(val binding: ViewItemSmallBinding) : RecyclerView.ViewHolder(binding.root)

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ViewHolder(
            ViewItemSmallBinding.inflate(
                LayoutInflater.from(parent.context),
                parent, false)
        )

        override fun onBindViewHolder(holder: ViewHolder, position: Int) = with(holder.binding) {
            button.visibility = INVISIBLE
            textView4.text = getItem(position)
        }

        private object SlappItemDiff : DiffUtil.ItemCallback<String>() {
            override fun areContentsTheSame(oldItem: String, newItem: String) = oldItem == newItem
            override fun areItemsTheSame(oldItem: String, newItem: String) = oldItem == newItem
        }
    }
}
