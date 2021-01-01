package com.klmn.slapp.ui

import android.view.ActionMode
import android.view.Menu
import android.view.MenuItem
import com.klmn.slapp.R
import com.klmn.slapp.common.SelectableListAdapter

class SelectionMode(private val adapter: SelectableListAdapter<*, *>) : ActionMode.Callback {
    override fun onCreateActionMode(mode: ActionMode?, menu: Menu?): Boolean {
        mode?.menuInflater?.inflate(R.menu.selection_menu, menu)
        return true
    }

    override fun onPrepareActionMode(mode: ActionMode?, menu: Menu?) = true

    override fun onActionItemClicked(mode: ActionMode?, item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.action_delete -> {
                println("deleted items")
                mode?.finish()
            }
        }
        return true
    }

    override fun onDestroyActionMode(mode: ActionMode?) = adapter.clearSelection()
}