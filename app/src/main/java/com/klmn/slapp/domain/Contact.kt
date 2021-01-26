package com.klmn.slapp.domain

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Contact(
    val phoneNumber: String,
    val displayName: String? = null,
) : Parcelable
