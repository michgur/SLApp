package com.klmn.slapp.domain

data class SlappItem(
    val name: String = "",
    val user: String = "",
    val timestamp: Long = System.currentTimeMillis() / 1000L
)

data class SlappList(
    val id: Long = 0,
    val name: String = "",
    val user: String = "",
    val timestamp: Long = System.currentTimeMillis() / 1000L,
    val items: MutableList<SlappItem> = mutableListOf(),
    val users: List<String> = listOf(user)
)