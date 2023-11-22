package com.bizify.data.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class Services(
    @SerializedName("itemid")
    @Expose
    var itemId: Int? = null,
    @SerializedName("itemName")
    @Expose
    var itemName: String? = null,
    @SerializedName("unitPrice")
    @Expose
    var unitPrice: String? = null,
    @SerializedName("unitName")
    @Expose
    var unitName: String? = null,
    @SerializedName("quantity")
    @Expose
    var quantity: String = "1",
)
