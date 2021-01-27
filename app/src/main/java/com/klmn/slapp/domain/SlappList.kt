package com.klmn.slapp.domain

data class SlappList(
    val id: String = "",
    val name: String = "",
    val user: Contact = Contact(""),
    val timestamp: Long = System.currentTimeMillis() / 1000L,
    val items: List<SlappItem> = listOf(),
    val users: List<Contact> = listOf(user)
)