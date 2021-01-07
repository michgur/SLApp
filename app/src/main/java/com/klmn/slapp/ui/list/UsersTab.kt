package com.klmn.slapp.ui.list

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.klmn.slapp.R
import com.klmn.slapp.databinding.TabUsersBinding
import com.klmn.slapp.databinding.ViewUserBinding
import com.klmn.slapp.domain.Contact
import com.klmn.slapp.domain.ContactDiff
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect

@ExperimentalCoroutinesApi
@AndroidEntryPoint
class UsersTab : Fragment() {
    private var _binding: TabUsersBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ListViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = TabUsersBinding.inflate(inflater, container, false)

        binding.usersRecyclerView.apply {
            adapter = UserAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }

        lifecycleScope.launchWhenStarted {
            viewModel.users.collect(UserAdapter::submitList)
        }

        return binding.root
    }

    private object UserAdapter : ListAdapter<Contact, UserAdapter.ViewHolder>(ContactDiff) {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
            ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.view_user, parent, false))

        override fun onBindViewHolder(holder: ViewHolder, position: Int) =
            ViewUserBinding.bind(holder.itemView).run {
                val user = getItem(position)
                textIcon.text = user.displayName ?: "?"
                textNumber.text = user.phoneNumber
                textUser.run {
                    if (user.displayName == null) visibility = GONE
                    else text = user.displayName
                }
            }

        class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
    }
}