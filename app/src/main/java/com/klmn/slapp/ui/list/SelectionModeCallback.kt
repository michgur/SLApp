package com.klmn.slapp.ui.list

import android.content.Context
import android.view.ActionMode
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import com.klmn.slapp.R
import com.klmn.slapp.domain.SlappItem
import com.klmn.slapp.common.MultiSelectListAdapter

class SelectionModeCallback(
    private val context: Context,
    private val viewModel: ListViewModel,
    private val adapter: MultiSelectListAdapter<SlappItem, *>
) : ActionMode.Callback {
    override fun onCreateActionMode(mode: ActionMode?, menu: Menu?): Boolean {
        mode?.menuInflater?.inflate(R.menu.selection_menu, menu)
        mode?.title = "1"
        return true
    }

    override fun onPrepareActionMode(mode: ActionMode?, menu: Menu?) = true

    override fun onActionItemClicked(mode: ActionMode?, item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.action_delete -> viewModel.selection.value?.apply {
                // should probably happen in a single callback since any change to th
                forEach(viewModel::deleteItem)
                Toast.makeText(
                    context,
                    context.resources.getString(R.string.toast_deleted, size),
                    Toast.LENGTH_SHORT
                ).show()
                clear()
            }
            R.id.action_select_all -> adapter.selectAll()
        }
        return true
    }

    override fun onDestroyActionMode(mode: ActionMode?) = adapter.clearSelection()
}