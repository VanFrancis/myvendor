package com.mobilepoc.myvendor.data.entites

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Client(
    val user_id: String = "",
    val name: String = "",
    val mobileNumber: String = "",

    val address: String = "",
    val zipCode: String = "",
    val additionalNote: String = "",

    var id: String = "",
) : Parcelable