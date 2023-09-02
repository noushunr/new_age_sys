package com.bizify.data.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class Customers(
    @SerializedName("cs_No")
    @Expose
    var id: Int? = null,
    @SerializedName("cs_Name")
    @Expose
    var csName: String? = null,
    @SerializedName("mobile")
    @Expose
    var mobile: String? = null,
    @SerializedName("message")
    @Expose
    var message: String? = null,
    @SerializedName("email")
    @Expose
    var email: String? = null,
    @SerializedName("cs_Reff_AC_No")
    @Expose
    var cs_Reff_AC_No: String? = null,
)
