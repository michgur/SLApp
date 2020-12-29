package com.klmn.slapp.ui

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.klmn.slapp.SLApp
import com.klmn.slapp.domain.SlappItem
import com.klmn.slapp.domain.SlappList

class ListViewModel(application: SLApp) : ViewModel() {
    private val repository = application.repository

    val list = MutableLiveData(SlappList(
            0,
        "family",
        "michael",
        0,
        mutableListOf(
            SlappItem("cucumber", "michael", 1),
            SlappItem("tomato", "michael", 2),
            SlappItem("onion", "michael", 3),
        ),
        listOf("michael")
    ))

    val user = MutableLiveData<String>()

    fun addItem(name: String) = repository.addItem(
        list.value?.id ?: 0,
        SlappItem(
            name,
            user.value ?: "",
            System.currentTimeMillis() / 1000L
        )
    )
}