package com.klmn.slapp.domain

data class TokenRequest(
    val operation: String,
    val notification_key_name: String,
    val notification_key: String,
    val registration_ids: List<String>
) {
    companion object {
        const val CREATE = "create"
        const val ADD = "add"
        const val REMOVE = "remove"
    }
}
