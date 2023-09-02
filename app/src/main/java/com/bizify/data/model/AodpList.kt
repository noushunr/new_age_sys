package com.bizify.data.model

import android.os.Parcel
import android.os.Parcelable
import androidx.annotation.NonNull
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

/**
 * Created by Noushad N on 05-06-2022.
 */
@Entity
data class AodpList(

    @NonNull
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    @SerializedName("id")
    @Expose
    var id: Int? = null,

    @ColumnInfo(name = "date")
    @SerializedName("date")
    var date: String? = null,

    @ColumnInfo(name = "explanation")
    @SerializedName("explanation")
    var explanation: String? = null,

    @ColumnInfo(name = "hdurl")
    @SerializedName("hdurl")
    var hdurl: String? = null,

    @ColumnInfo(name = "media_type")
    @SerializedName("media_type")
    var media_type: String? = null,

    @ColumnInfo(name = "service_version")
    @SerializedName("service_version")
    var service_version: String? = null,

    @ColumnInfo(name = "title")
    @SerializedName("title")
    var title: String? = null,

    @ColumnInfo(name = "url")
    @SerializedName("url")
    var url: String? = null,
): Parcelable{
    constructor(parcel: Parcel) : this(
        parcel.readValue(Int::class.java.classLoader) as? Int,
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeValue(id)
        parcel.writeString(date)
        parcel.writeString(explanation)
        parcel.writeString(hdurl)
        parcel.writeString(media_type)
        parcel.writeString(service_version)
        parcel.writeString(title)
        parcel.writeString(url)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<AodpList> {
        override fun createFromParcel(parcel: Parcel): AodpList {
            return AodpList(parcel)
        }

        override fun newArray(size: Int): Array<AodpList?> {
            return arrayOfNulls(size)
        }
    }

}
