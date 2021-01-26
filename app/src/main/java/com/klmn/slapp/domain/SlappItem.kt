package com.klmn.slapp.domain

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class SlappItem(
    val name: String = "",
    val user: Contact = Contact(""),
    val timestamp: Long = System.currentTimeMillis() / 1000L
) : Parcelable