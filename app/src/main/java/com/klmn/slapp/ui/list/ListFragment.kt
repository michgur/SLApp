package com.klmn.slapp.ui.list

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.navigation.ui.setupWithNavController
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.google.android.material.transition.MaterialSharedAxis
import com.klmn.slapp.R
import com.klmn.slapp.databinding.FragmentListBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi

@ExperimentalCoroutinesApi
@AndroidEntryPoint
class ListFragment : Fragment() {
    private var _binding: FragmentListBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ListViewModel by activityViewModels()
    private val args: ListFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentListBinding.inflate(inflater, container, false)

        viewModel.listId.value = args.listId

        enterTransition = MaterialSharedAxis(MaterialSharedAxis.Z, true).setDuration(500L)
        returnTransition = MaterialSharedAxis(MaterialSharedAxis.Z, false).setDuration(500L)

        // setup toolbar with list title and connect to activity & navController
        binding.toolbar.apply {
            viewModel.listName.observe(viewLifecycleOwner, ::setTitle)
            setupWithNavController(findNavController())
            (requireActivity() as AppCompatActivity).setSupportActionBar(this)
        }

        binding.tabContainer.adapter = TabAdapter(this)
        // the tabs are already configured, only need to be attached to the ViewPager2
        TabLayoutMediator(binding.tabLayout, binding.tabContainer) { tab, position ->
            if (position == 0) tab.setText(R.string.tab_items)
            else tab.setText(R.string.tab_users)
        }.attach()

        // kill selection action mode on tab switch
        binding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}
            override fun onTabUnselected(tab: TabLayout.Tab?) {
                viewModel.selectionModeEnabled.value = false
            }
        })

        return binding.root
    }

    private class TabAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {
        override fun getItemCount() = 2
        override fun createFragment(position: Int): Fragment = when(position) {
            0 -> ItemsTab()
            1 -> UsersTab()
            else -> throw IllegalArgumentException("there are only 2 tabs")
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}