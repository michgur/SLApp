package com.klmn.slapp.domain

data class SlappItem(
    val name: String = "",
    val user: Contact = Contact("", ""),
    val timestamp: Long = System.currentTimeMillis() / 1000L
)

data class SlappList(
    val id: String = "",
    val name: String = "",
    val user: Contact = Contact("", ""),
    val timestamp: Long = System.currentTimeMillis() / 1000L,
    val items: MutableList<SlappItem> = mutableListOf(),
    val users: List<Contact> = listOf(user)
)