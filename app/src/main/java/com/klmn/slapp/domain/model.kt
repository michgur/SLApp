package com.klmn.slapp

data class SlappItem(
    val name: String,
    val user: String,
    val timestamp: Long
)

data class SlappList(
    val name: String,
    val user: String,
    val timestamp: Long,
    val items: MutableList<SlappItem>,
    val users: List<String>
)