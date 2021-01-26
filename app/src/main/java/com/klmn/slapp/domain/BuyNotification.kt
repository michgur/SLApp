package com.klmn.slapp.domain

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class BuyNotification(
    val listId: String,
    val listName: String,
    val uid: String,
    val timestamp: Long,
    val items: List<SlappItem>
) : Parcelable
