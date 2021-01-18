package com.klmn.slapp.ui.list.info

import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.navigation.ui.setupWithNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.transition.Slide
import androidx.transition.Transition
import com.klmn.slapp.databinding.FragmentListInfoBinding
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
        }

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
}