package com.example.homequest.Models

import android.os.Parcel
import android.os.Parcelable

data class Listing(
    val id: String = "",
    val name: String = "",
    val address: String = "",
    val price: String = "",
    val description: String = "", // Add the description field
    val imageUrls: List<String> = emptyList(),
    val sellerId: String = ""
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.createStringArrayList() ?: emptyList(),
        parcel.readString() ?: ""
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id)
        parcel.writeString(name)
        parcel.writeString(address)
        parcel.writeString(price)
        parcel.writeString(description)
        parcel.writeStringList(imageUrls)
        parcel.writeString(sellerId)
    }

    override fun describeContents() = 0

    companion object CREATOR : Parcelable.Creator<Listing> {
        override fun createFromParcel(parcel: Parcel) = Listing(parcel)
        override fun newArray(size: Int) = arrayOfNulls<Listing>(size)
    }
}
