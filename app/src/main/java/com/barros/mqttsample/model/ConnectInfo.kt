package com.barros.mqttsample.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ConnectInfo(
    val serverURI: String,
    val clientID: String,
    val username: String,
    val password: String
) : Parcelable
