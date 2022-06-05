package com.example.newagesys.utils

import android.content.ContentResolver
import android.content.Context
import android.graphics.Bitmap
import android.util.Base64
import android.graphics.BitmapFactory
import android.media.MediaScannerConnection
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.NetworkInfo
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.os.Handler
import android.os.Looper
import android.text.Html
import android.text.Spanned
import android.util.Log
import android.view.View
import android.webkit.MimeTypeMap
import androidx.core.app.NotificationCompat
import androidx.core.content.FileProvider
import com.example.newagesys.MyApplication
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.*
import okhttp3.RequestBody
import java.io.*
import java.net.URL
import java.text.SimpleDateFormat
import java.util.*
import okhttp3.MediaType
import okio.BufferedSink

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