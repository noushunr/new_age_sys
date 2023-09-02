package com.bizify.utils

import android.content.Context
import android.net.ConnectivityManager
import java.text.SimpleDateFormat
import java.util.*


fun formatDate(date: String): String {
    var formattedString = date
    val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    var newDate : Date? = null
    try {
        newDate = dateFormat.parse(date)
        formattedString = SimpleDateFormat("MMMM dd, yyyy", Locale.getDefault()).format(newDate!!)
    } catch (e: Exception) {
        ////println(e.message)
    }
    return formattedString
}
fun isConnected(context: Context): Boolean {
    val connectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val netInfo = connectivityManager.activeNetworkInfo
    return netInfo != null && netInfo.isConnected
}