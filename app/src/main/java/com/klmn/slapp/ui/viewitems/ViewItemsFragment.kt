package com.klmn.slapp.ui.viewitems

import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.navigation.ui.setupWithNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.transition.Slide
import com.klmn.slapp.R
import com.klmn.slapp.common.BoundViewHolder
import com.klmn.slapp.common.formatTimeStamp
import com.klmn.slapp.data.contacts.ContactProvider
import com.klmn.slapp.databinding.FragmentListItemsBinding
import com.klmn.slapp.databinding.ViewItemBinding
import com.klmn.slapp.domain.SlappItem
import com.klmn.slapp.domain.SlappItemDiff
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class ViewItemsFragment : Fragment() {
    private var _binding : FragmentListItemsBinding? = null
    private val binding get() = _binding!!

    private val args: ViewItemsFragmentArgs by navArgs()
    @Inject lateinit var contactProvider: ContactProvider

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentListItemsBinding.inflate(inflater, container, false)

        enterTransition = Slide(Gravity.END).setDuration(500L)
        returnTransition = Slide(Gravity.END).setDuration(500L)

        val navUp = { findNavController().navigate(R.id.action_viewItemsFragment_to_homeFragment) }
        binding.toolbar.apply {
            setupWithNavController(findNavController())
            (requireActivity() as AppCompatActivity).let {
                it.setSupportActionBar(this)
                it.supportActionBar?.setDisplayHomeAsUpEnabled(true)
                it.onBackPressedDispatcher.addCallback(viewLifecycleOwner) { navUp() }
            }
            setNavigationOnClickListener { navUp() }
        }

        binding.recyclerViewItems.apply {
            adapter = Adapter().apply { submitList(args.notification.items) }
            layoutManager = LinearLayoutManager(requireContext())
        }

        binding.textListName.text = args.notification.listName
        binding.textSubtitle.text = getString(
            R.string.view_items_subtitle,
            args.notification.uid.let { contactProvider.getContact(it)?.displayName ?: it },
            formatTimeStamp(args.notification.timestamp)
        )

        binding.viewItemInput.root.isVisible = false
        binding.bottomSheetShop.root.isVisible = false

        return binding.root
    }

    private class Adapter : ListAdapter<SlappItem, Adapter.ViewHolder>(SlappItemDiff) {
        class ViewHolder(parent: ViewGroup) : BoundViewHolder<ViewItemBinding>(parent, ViewItemBinding::inflate)

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ViewHolder(parent)
        override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.binding.run {
            val item = currentList[position]
            textName.text = item.name
            textTime.text = formatTimeStamp(item.timestamp)
            textUser.text = item.user.displayName ?: item.user.phoneNumber

            if (position > 0) {
                val differentUser = currentList[position - 1].user != item.user
                textUser.isVisible = differentUser
                divider.isVisible = differentUser && position > 0
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}