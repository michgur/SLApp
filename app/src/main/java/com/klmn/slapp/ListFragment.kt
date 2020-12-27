package com.klmn.slapp

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.*
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.transition.TransitionManager
import com.google.android.material.transition.MaterialContainerTransform
import com.klmn.slapp.databinding.FragmentListBinding

class ListFragment : Fragment() {
    private var _binding: FragmentListBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ListViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentListBinding.inflate(inflater, container, false)

        binding.itemsRecyclerView.apply {
            viewModel.list.observe(viewLifecycleOwner) {
                (adapter as SlappListAdapter).submitList(it.items)
            }

            adapter = SlappListAdapter()
            layoutManager = LinearLayoutManager(requireContext())
        }

        binding.addFab.useCompatPadding = false
        binding.addFab.setOnClickListener { toggleAddFab() }
        binding.newItemView.sendFab.setOnClickListener { toggleAddFab() }

        return binding.root
    }

    private fun toggleAddFab() {
        val views =
            if (binding.addFab.visibility == VISIBLE)
                binding.addFab to binding.newItemView.root
            else binding.newItemView.root to binding.addFab
        val anim = MaterialContainerTransform().apply {
            startView = views.first
            endView = views.second
            addTarget(views.second)
            scrimColor = Color.TRANSPARENT
            duration = 500
        }
        TransitionManager.beginDelayedTransition(binding.root, anim)
        views.first.visibility = INVISIBLE
        views.second.visibility = VISIBLE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}