package com.klmn.slapp.domain

data class BuyNotification(
    val listId: String,
    val uid: String,
    val timestamp: Long,
    val items: List<SlappItem>
)
