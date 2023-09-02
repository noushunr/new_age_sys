package com.bizify.data.model

import com.google.gson.annotations.SerializedName

data class LoginModel(
    @field:SerializedName("userName")
    var username: String? = null,
    @field:SerializedName("password")
    var password:String?=null
) {
}