package com.klmn.slapp.ui.list.users

import android.app.SearchManager
import android.content.Context
import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat.getSystemService
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.navigation.ui.setupWithNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.klmn.slapp.R
import com.klmn.slapp.common.MultiSelectListAdapter
import com.klmn.slapp.databinding.FragmentAddUsersBinding
import com.klmn.slapp.domain.Contact
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect

@ExperimentalCoroutinesApi
@AndroidEntryPoint
class AddUsersFragment : Fragment(), MultiSelectListAdapter.Callback<Contact> {
    private var _binding: FragmentAddUsersBinding? = null
    private val binding get() = _binding!!

    private val args: AddUsersFragmentArgs by navArgs()
    private val viewModel: AddUsersViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddUsersBinding.inflate(inflater, container, false)

        binding.toolbar.apply {
            setupWithNavController(findNavController())
            (requireActivity() as AppCompatActivity).setSupportActionBar(this)
        }

        val adapter = AddUsersAdapter(viewModel.selection).also {
            it.addSelectionListener(this)
        }
        binding.usersRecyclerView.adapter = adapter
        binding.usersRecyclerView.layoutManager = LinearLayoutManager(requireContext())

        lifecycleScope.launchWhenStarted {
            viewModel.contacts.collect(adapter::submitList)
        }

        binding.fabDone.setOnClickListener {
            viewModel.addUsers(args.listId)
            findNavController().navigateUp()
        }

        return binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.search_menu, menu)
    }

    override fun onItemStateChanged(item: Contact, selected: Boolean) {
        viewModel.selection.apply {
            if (selected) add(item)
            else remove(item)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}