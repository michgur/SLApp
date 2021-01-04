package com.klmn.slapp.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.INVISIBLE
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.klmn.slapp.databinding.ViewItemSmallBinding
import com.klmn.slapp.databinding.ViewListSmallBinding

class ListPreviewFragment : Fragment() {
    private var _binding: ViewListSmallBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = ViewListSmallBinding.inflate(inflater, container, false)

        binding.textView.text = requireArguments().getString("listName")
        binding.itemsRecyclerView.apply {
            val a = SmallItemAdapter()
            val lm = LinearLayoutManager(requireContext())
            adapter = a
            layoutManager = lm
            a.submitList(requireArguments().getStringArrayList("items")) {
                lm.scrollToPositionWithOffset(a.itemCount - 1, 0)
            }
        }
        return binding.root
    }

    class SmallItemAdapter : ListAdapter<String, SmallItemAdapter.ViewHolder>(SlappItemDiff) {
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

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}