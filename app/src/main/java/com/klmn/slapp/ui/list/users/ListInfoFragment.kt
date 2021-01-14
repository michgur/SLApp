package com.klmn.slapp.ui.list.users

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.klmn.slapp.R
import com.klmn.slapp.databinding.FragmentListInfoBinding
import com.klmn.slapp.databinding.ViewUserBinding
import com.klmn.slapp.domain.Contact
import com.klmn.slapp.domain.ContactDiff
import com.klmn.slapp.ui.list.ListViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect

@ExperimentalCoroutinesApi
@AndroidEntryPoint
class ListInfoFragment : Fragment() {
    private var _binding: FragmentListInfoBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ListViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentListInfoBinding.inflate(inflater, container, false)

        binding.toolbar.apply {
            viewModel.listName.observe(viewLifecycleOwner, ::setTitle)
            setupWithNavController(findNavController())
            (requireActivity() as AppCompatActivity).setSupportActionBar(this)
        }

        binding.usersRecyclerView.apply {
            adapter = UserAdapter()
            layoutManager = LinearLayoutManager(requireContext())
            lifecycleScope.launchWhenStarted {
                viewModel.users.collect { (adapter as UserAdapter).submitList(it) }
            }
        }

        return binding.root
    }

    private inner class UserAdapter : ListAdapter<Contact, UserAdapter.ViewHolder>(ContactDiff) {
        // first item in the list is the add users button
        override fun getItemCount() = super.getItemCount() + 1
        override fun getItemViewType(position: Int) = if (position == 0) 1 else 0

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
            ViewHolder(LayoutInflater.from(parent.context).inflate(
                if (viewType == 0) R.layout.view_user else R.layout.view_add_users,
                parent,
                false
            ))

        override fun onBindViewHolder(holder: ViewHolder, position: Int) =
            if (holder.itemViewType == 0) ViewUserBinding.bind(holder.itemView).run {
                val contact = getItem(position - 1)
                if (contact.displayName == null) {
                    textIcon.text = "?"
                    textUser.text = contact.phoneNumber
                    textNumber.visibility = GONE
                } else {
                    textIcon.text = contact.displayName
                    textUser.text = contact.displayName
                    textNumber.visibility = View.VISIBLE
                    textNumber.text = contact.phoneNumber
                }
            } else holder.itemView.setOnClickListener {
                findNavController().navigate(
                    ListInfoFragmentDirections.actionListInfoFragmentToAddUsersFragment(viewModel.listId.value)
                )
            }

        inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
    }
}