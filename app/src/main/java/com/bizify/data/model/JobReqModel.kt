package com.bizify.data.model

import com.google.gson.annotations.SerializedName

data class JobReqModel(
    @field:SerializedName("cust_Id")
    var cust_Id: Int? = 0,
    @field:SerializedName("service_ID")
    var service_ID:Int?=0,
    @field:SerializedName("start_Date")
    var start_Date: String? = null,
    @field:SerializedName("v_Date")
    var v_Date:String? = null,
    @field:SerializedName("jobType")
    var jobType: String? = null,
    @field:SerializedName("odoMeter")
    var odoMeter: String? = null,
    @field:SerializedName("customer_Complaint")
    var customer_Complaint: String? = null,
    @field:SerializedName("tech_Inspection")
    var tech_Inspection: String? = "",
    @field:SerializedName("userID")
    var userID: Int? = 0,
    @field:SerializedName("tech_Complettion")
    var tech_Complettion: String? = "",
    @field:SerializedName("plate_ID")
    var plate_ID: Int? = 0,
    @field:SerializedName("registartion")
    var registartion: String? = null,
    @field:SerializedName("refNo")
    var refNo: String? = "",
    @field:SerializedName("status")
    var status: String? = "IN PROGRESS",
    @field:SerializedName("carTop_Remarks")
    var carTopRemarks: String? = "",
    @field:SerializedName("carRight_Remarks")
    var carRightRemarks: String? = "",
    @field:SerializedName("carLeft_Remarks")
    var carLeftRemarks: String? = "",
    @field:SerializedName("carRear_Remarks")
    var carRearRemarks: String? = "",
    @field:SerializedName("carBottom_Remarks")
    var carBottomRemarks: String? = "",
    @field:SerializedName("carFront_Remarks")
    var carFrontRemarks: String? = ""
) {
}