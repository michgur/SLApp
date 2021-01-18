package com.klmn.slapp.domain

data class SlappItem(
    val name: String = "",
    val user: Contact = Contact(""),
    val timestamp: Long = System.currentTimeMillis() / 1000L
)