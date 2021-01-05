package com.klmn.slapp.ui.create

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.klmn.slapp.databinding.FragmentCreateListBinding

class CreateListFragment : Fragment() {
    private var _binding: FragmentCreateListBinding? = null
    private val binding get() = _binding!!

    private val viewModel: CreateListViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCreateListBinding.inflate(inflater, container, false)

        binding.textInputLayout.editText?.doAfterTextChanged {
            binding.createListBtn.isEnabled = !it.isNullOrEmpty()
        }

        binding.createListBtn.setOnClickListener {
            binding.textInputLayout.editText?.let {
                viewModel.createList(it.text.toString())
                    .doOnSuccess { id ->
                        // todo: pop this from backstack
                        findNavController().navigate(
                            CreateListFragmentDirections
                                .actionCreateListFragmentToListFragment(id)
                        )
                    }
                    .doOnException {
                        Toast.makeText(
                            requireContext(),
                            "Failed To Create List",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    .execute()
            }
        }

        return binding.root
    }
}