package com.bizify.data.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class Vehicles(
    @SerializedName("veh_ID")
    @Expose
    var id: Int? = null,
    @SerializedName("veh_Name")
    @Expose
    var veh_Name: String? = null,
    @SerializedName("veh_Brand")
    @Expose
    var veh_Brand: String? = null,
    @SerializedName("veh_Model")
    @Expose
    var veh_Model: String? = null,
    @SerializedName("registartion")
    @Expose
    var registartion: String? = null,
    @SerializedName("engineNo")
    @Expose
    var engineNo: String? = null,
    @SerializedName("chasisNo")
    @Expose
    var chasisNo: String? = null,
    @SerializedName("message")
    @Expose
    var message: String? = null,

)
