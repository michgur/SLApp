package com.klmn.slapp.ui.list.items

import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import androidx.navigation.fragment.findNavController
import com.klmn.slapp.R

class ExitDialogFragment : DialogFragment() {
    override fun onCreateDialog(savedInstanceState: Bundle?) = AlertDialog.Builder(requireContext())
        .setTitle(R.string.exit_dialog_title)
        .setMessage(R.string.exit_dialog_message)
        .setPositiveButton(R.string.exit_dialog_pos) { _, _ ->
            findNavController().navigate(R.id.action_exitDialogFragment_to_homeFragment)
        }.setNegativeButton(R.string.exit_dialog_neg) { _, _ ->
            findNavController().navigateUp()
        }
        .create()
}