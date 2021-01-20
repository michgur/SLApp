package com.klmn.slapp.domain

data class Contact(
    val phoneNumber: String,
    val displayName: String? = null,
    val registrationToken: String? = null
)
