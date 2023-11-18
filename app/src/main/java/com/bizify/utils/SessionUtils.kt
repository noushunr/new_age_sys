package com.bizify.utils

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.core.content.ContextCompat.startActivity
import androidx.core.content.edit
import com.google.gson.Gson
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName


class SessionUtils {
    private var preferences: SharedPreferences? = null
    constructor(
        context: Context

    ){
        preferences =
        context.getSharedPreferences("Bitzify", Context.MODE_PRIVATE)
    }
    companion object {


        fun init(context: Context) {


        }




    }

    fun saveToken(token: String?) {
        preferences!!.edit().putString("token", token).apply()
    }


    val token: String?
        get() = preferences!!.getString("token", "")

    fun saveUserId(userId: String?) {
        preferences!!.edit().putString("userId", userId).apply()
    }


    val userId: String?
        get() = preferences!!.getString("userId", "")

    fun saveURL(url: String?) {
        preferences!!.edit().putString("base_url", url).apply()
    }

    val BASE_URL: String?
        get() = preferences!!.getString("base_url", "http://3.87.213.2:80/")
}