package com.bizify.data.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class User(
    @SerializedName("userId")
    @Expose
    var id: Int? = null,
    @SerializedName("userName")
    @Expose
    var userName: String? = null,
    @SerializedName("token")
    @Expose
    var token: String? = null,
    @SerializedName("message")
    @Expose
    var message: String? = null,
    @SerializedName("workGroupId")
    @Expose
    var workGroupId: Int? = null,
    @SerializedName("isSuccess")
    @Expose
    var isSuccess: Boolean? = false,
)
