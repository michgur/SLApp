package com.klmn.slapp.common

import android.text.format.DateFormat
import android.text.format.DateUtils
import java.util.*

fun formatTimeStamp(ts: Long): String {
    val millis = ts * 1000L
    if (DateUtils.isToday(millis)) return DateFormat.format("HH:mm", millis).toString()
    if (DateUtils.isToday(millis + DateUtils.DAY_IN_MILLIS)) return "yesterday"
    val c = Calendar.getInstance()
    val year = c.get(Calendar.YEAR)
    c.time = Date(millis)
    if (year == c.get(Calendar.YEAR)) return DateFormat.format("MMM dd", millis).toString()
    return DateFormat.format("dd.MM.yy", millis).toString()
}
