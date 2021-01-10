package com.klmn.slapp.ui.create

import android.graphics.Color.TRANSPARENT
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.transition.Slide
import com.google.android.material.transition.MaterialContainerTransform
import com.klmn.slapp.R
import com.klmn.slapp.SLApp
import com.klmn.slapp.databinding.FragmentCreateListBinding
import com.klmn.slapp.ui.home.HomeViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi

@ExperimentalCoroutinesApi
@AndroidEntryPoint
class CreateListFragment : Fragment() {
    private var _binding: FragmentCreateListBinding? = null
    private val binding get() = _binding!!

    private val viewModel: HomeViewModel by activityViewModels()

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

        binding.fieldListName.editText?.doAfterTextChanged {
            binding.createListBtn.isEnabled = !it.isNullOrBlank()
        }

        binding.createListBtn.setOnClickListener {
            binding.fieldListName.editText?.let {
                viewModel.createList(it.text.toString()) {
                    (requireActivity().application as SLApp).mainThread.execute {
                        findNavController().navigate(
                            CreateListFragmentDirections
                                .actionCreateListFragmentToListFragment(it)
                        )
                    }
                }
            }
        }

        return binding.root
    }
}