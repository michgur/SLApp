package com.klmn.slapp.domain

data class SlappItem(
    val name: String,
    val user: String,
    val timestamp: Long
)

data class SlappList(
    val id: Int = 0,
    val name: String,
    val user: String,
    val timestamp: Long,
    val items: MutableList<SlappItem>,
    val users: List<String>
)