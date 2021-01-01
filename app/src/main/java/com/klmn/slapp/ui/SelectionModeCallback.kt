package com.klmn.slapp.ui

import android.view.ActionMode
import android.view.Menu
import android.view.MenuItem
import com.klmn.slapp.R
import com.klmn.slapp.common.SelectableListAdapter
import com.klmn.slapp.domain.SlappItem

class SelectionModeCallback(
    private val viewModel: ListViewModel,
    private val adapter: SelectableListAdapter<SlappItem, *>
) : ActionMode.Callback {
    override fun onCreateActionMode(mode: ActionMode?, menu: Menu?): Boolean {
        mode?.menuInflater?.inflate(R.menu.selection_menu, menu)
        return true
    }

    override fun onPrepareActionMode(mode: ActionMode?, menu: Menu?) = true

    override fun onActionItemClicked(mode: ActionMode?, item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.action_delete -> {
                adapter.selection().forEach(viewModel::deleteItem)
                mode?.finish()
            }
            R.id.action_select_all -> adapter.selectAll()
        }
        return true
    }

    override fun onDestroyActionMode(mode: ActionMode?) = adapter.clearSelection()
}