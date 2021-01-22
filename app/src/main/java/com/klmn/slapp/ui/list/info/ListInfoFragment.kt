package com.klmn.slapp.ui.list.info

import android.os.Bundle
import android.view.*
import android.view.inputmethod.EditorInfo
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.navigation.ui.setupWithNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.transition.Slide
import com.klmn.slapp.R
import com.klmn.slapp.databinding.FragmentListInfoBinding
import com.klmn.slapp.databinding.ViewEditTitleBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect

@ExperimentalCoroutinesApi
@AndroidEntryPoint
class ListInfoFragment : Fragment() {
    private var _binding: FragmentListInfoBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ListInfoViewModel by viewModels()
    private val args: ListInfoFragmentArgs by navArgs()

    private lateinit var titleActionView: ViewEditTitleBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentListInfoBinding.inflate(inflater, container, false)

        enterTransition = Slide(Gravity.END).excludeTarget(binding.appBarLayout, true)
        exitTransition = Slide(Gravity.END).excludeTarget(binding.appBarLayout, true)

        viewModel.listId.value = args.listId

        binding.toolbar.apply {
            viewModel.listName.observe(viewLifecycleOwner, ::setTitle)
            setupWithNavController(findNavController())
            (requireActivity() as AppCompatActivity).setSupportActionBar(this)
            setOnMenuItemClickListener {
                titleActionView.fieldListName.setText(viewModel.listName.value)
                true
            }
        }
        setHasOptionsMenu(true)

        binding.usersRecyclerView.apply {
            adapter = ListUsersAdapter {
                // the adapter also controls the add Users button. on click, nav to addUsersFragment
                findNavController().navigate(
                    ListInfoFragmentDirections
                        .actionListInfoFragmentToAddUsersFragment(viewModel.listId.value)
                )
            }
            layoutManager = LinearLayoutManager(requireContext())
            lifecycleScope.launchWhenStarted {
                viewModel.users.collect { (adapter as ListUsersAdapter).submitList(it) }
            }
        }

        return binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.edit_menu, menu)
        titleActionView = ViewEditTitleBinding.inflate(layoutInflater)
        menu.findItem(R.id.action_edit).let { menuItem ->
            menuItem.actionView = titleActionView.root
            titleActionView.apply {
                fieldListName.setOnEditorActionListener { _, actionId, _ ->
                    (actionId == EditorInfo.IME_ACTION_DONE).also {
                        if (it) editTitle(menuItem)
                    }
                }
                fieldListName.doAfterTextChanged { btnDone.isVisible = !it.isNullOrBlank() }
                btnDone.setOnClickListener { editTitle(menuItem) }
                btnCancel.setOnClickListener { menuItem.collapseActionView() }
            }
        }
    }

    private fun editTitle(menuItem: MenuItem) {
        val name = titleActionView.fieldListName.text.toString()
        if (name.isNotBlank() && name != viewModel.listName.value) {
            viewModel.setListName(name)
            menuItem.collapseActionView()
        }
    }
}
