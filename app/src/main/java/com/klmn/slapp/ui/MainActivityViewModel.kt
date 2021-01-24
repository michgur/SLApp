package com.klmn.slapp.ui

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import com.klmn.slapp.data.datastore.UserPreferences
import kotlinx.coroutines.launch

class MainActivityViewModel @ViewModelInject constructor(
    private val userPreferences: UserPreferences
) : ViewModel() {
    val shouldAuthenticate = userPreferences.phoneNumber.map(String::isBlank)
    val shouldRequestContactsPermission = userPreferences.hasReadContactsPermission.map { !it }

    fun saveHasContactsPermission(hasPermission: Boolean) = viewModelScope.launch {
        userPreferences.saveHasReadContactsPermission(hasPermission)
    }
}