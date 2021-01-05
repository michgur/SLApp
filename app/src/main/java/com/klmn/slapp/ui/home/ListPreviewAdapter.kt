package com.klmn.slapp.ui.home

import android.animation.AnimatorInflater
import android.view.LayoutInflater
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

class ListPreviewAdapter(private val home: Fragment) :
    RecyclerView.Adapter<ListPreviewAdapter.ViewHolder>() {
    private val lists = mutableListOf<SlappList>()

    fun addList(list: SlappList) {
        lists.add(list)
        notifyItemInserted(lists.size - 1)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ViewHolder(parent).apply {
        binding.apply {
            itemsRecyclerView.apply {
                adapter = MiniItemAdapter()
                layoutManager = LinearLayoutManager(home.requireContext())
            }
            button.setOnClickListener {
                AnimatorInflater.loadAnimator(home.requireContext(), R.animator.preview_scale).apply {
                    setTarget(root)
                    doOnEnd {
                        home.findNavController().navigate(
                            HomeFragmentDirections.actionHomeFragmentToListFragment(id)
                        )
                    }
                }.start()
            }
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.binding.run {
        toolbar.title = lists[position].name
        (itemsRecyclerView.adapter as MiniItemAdapter).submitList(
            lists[position].items.map(SlappItem::name)
        )
    }

    override fun getItemCount() = lists.size

    class ViewHolder(parent: ViewGroup) : RecyclerView.ViewHolder(
        LayoutInflater.from(parent.context).inflate(R.layout.view_list_small, parent, false)
    ) {
        var id: Long = 0L
        val binding = ViewListSmallBinding.bind(itemView)
    }

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
