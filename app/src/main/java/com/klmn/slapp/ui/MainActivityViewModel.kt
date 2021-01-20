package com.klmn.slapp.ui

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asFlow
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import com.klmn.slapp.data.SlappRepository
import com.klmn.slapp.data.datastore.UserPreferences
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class MainActivityViewModel @ViewModelInject constructor(
    private val userPreferences: UserPreferences,
    private val repository: SlappRepository
) : ViewModel() {
    val shouldAuthenticate = userPreferences.phoneNumber.map(String::isBlank)
    val shouldRequestContactsPermission = userPreferences.hasReadContactsPermission.map { !it }

    init {
        viewModelScope.launch {
            userPreferences.registrationToken.collect { token ->
                userPreferences.phoneNumber.asFlow().first { it.isNotBlank() }.let { number ->
                    repository.refreshToken(number, token)
                }
            }
        }
    }

    fun saveHasContactsPermission(hasPermission: Boolean) = viewModelScope.launch {
        userPreferences.saveHasReadContactsPermission(hasPermission)
    }
}