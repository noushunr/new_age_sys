package com.bizify.data.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class VehicleRequestModel(
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
    @SerializedName("vImage")
    @Expose
    var vImage : ByteArray?= null,
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as VehicleRequestModel

        if (veh_Name != other.veh_Name) return false
        if (veh_Brand != other.veh_Brand) return false
        if (veh_Model != other.veh_Model) return false
        if (registartion != other.registartion) return false
        if (engineNo != other.engineNo) return false
        if (chasisNo != other.chasisNo) return false
        if (vImage != null) {
            if (other.vImage == null) return false
            if (!vImage.contentEquals(other.vImage)) return false
        } else if (other.vImage != null) return false

        return true
    }

    override fun hashCode(): Int {
        var result = veh_Name?.hashCode() ?: 0
        result = 31 * result + (veh_Brand?.hashCode() ?: 0)
        result = 31 * result + (veh_Model?.hashCode() ?: 0)
        result = 31 * result + (registartion?.hashCode() ?: 0)
        result = 31 * result + (engineNo?.hashCode() ?: 0)
        result = 31 * result + (chasisNo?.hashCode() ?: 0)
        result = 31 * result + (vImage?.contentHashCode() ?: 0)
        return result
    }
}
