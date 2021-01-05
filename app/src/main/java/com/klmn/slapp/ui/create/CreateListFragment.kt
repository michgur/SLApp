package com.klmn.slapp.ui.create

import android.graphics.Color.TRANSPARENT
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.transition.Slide
import com.google.android.material.transition.MaterialContainerTransform
import com.klmn.slapp.R
import com.klmn.slapp.SLApp
import com.klmn.slapp.databinding.FragmentCreateListBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
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

        enterTransition = MaterialContainerTransform().apply {
            startView = requireActivity().findViewById(R.id.fab_add)
            endView = binding.root
            scrimColor = TRANSPARENT
            containerColor = resources.getColor(R.color.primaryColor)
            startContainerColor = resources.getColor(R.color.secondaryColor)
            endContainerColor = resources.getColor(R.color.primaryColor)
        }
        returnTransition = Slide().addTarget(binding.root)

        binding.textInputLayout.editText?.doAfterTextChanged {
            binding.createListBtn.isEnabled = !it.isNullOrEmpty()
        }

        binding.createListBtn.setOnClickListener {
            binding.textInputLayout.editText?.let {
                viewModel.createList(it.text.toString())
                    .doOnSuccess { id ->
                        (requireActivity().application as SLApp).mainThread.execute {
                            // todo: pop this from backstack
                            findNavController().navigate(
                                CreateListFragmentDirections
                                    .actionCreateListFragmentToListFragment(id)
                            )
                        }
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