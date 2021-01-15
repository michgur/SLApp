package com.klmn.slapp.ui.list.info.addUsers

import android.graphics.Color
import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.navigation.ui.setupWithNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.transition.Slide
import com.google.android.material.transition.MaterialContainerTransform
import com.klmn.slapp.R
import com.klmn.slapp.common.MultiSelectListAdapter
import com.klmn.slapp.databinding.FragmentAddUsersBinding
import com.klmn.slapp.domain.Contact
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect

@ExperimentalCoroutinesApi
@AndroidEntryPoint
class AddUsersFragment : Fragment(), MultiSelectListAdapter.Callback<Contact>,
    SearchView.OnQueryTextListener {
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

        enterTransition = MaterialContainerTransform().apply {
            startView = requireActivity().findViewById(R.id.view_add_users)
            endView = binding.root
            scrimColor = Color.TRANSPARENT
            setAllContainerColors(resources.getColor(R.color.primaryColor))
        }
        returnTransition = Slide()

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
        binding.fabDone.hide()

        binding.toolbar.apply {
            setupWithNavController(findNavController())
            (requireActivity() as AppCompatActivity).setSupportActionBar(this)
            setOnMenuItemClickListener {
                (it.actionView as SearchView).setOnQueryTextListener(this@AddUsersFragment)
                true
            }
        }
        setHasOptionsMenu(true)
        return binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.search_menu, menu)
    }

    override fun onItemStateChanged(item: Contact, selected: Boolean) {
        viewModel.selection.apply {
            if (selected) add(item)
            else remove(item)

            if (isEmpty()) binding.fabDone.hide()
            else binding.fabDone.show()
        }
    }

    override fun onQueryTextSubmit(query: String?) = true

    override fun onQueryTextChange(newText: String?): Boolean {
        viewModel.query.value = newText
        return true
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}