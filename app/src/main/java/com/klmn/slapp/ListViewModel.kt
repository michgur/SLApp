package com.klmn.slapp

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ListViewModel : ViewModel() {
    var list: LiveData<SlappList> = MutableLiveData(
        SlappList(
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
}