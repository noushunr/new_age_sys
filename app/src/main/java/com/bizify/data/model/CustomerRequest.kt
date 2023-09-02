package com.bizify.data.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class CustomerRequest(

    @SerializedName("cs_Name")
    @Expose
    var csName: String? = null,
    @SerializedName("mobile")
    @Expose
    var mobile: String? = null,

    @SerializedName("email")
    @Expose
    var email: String? = null,

)
