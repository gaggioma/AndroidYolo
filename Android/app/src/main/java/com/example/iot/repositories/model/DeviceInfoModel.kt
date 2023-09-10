package com.example.iot.repositories.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class DeviceInfoModel(

    @SerialName("pin_12")
    val pin12: Int,

    @SerialName("pin_13")
    val pin13: Int,

    @SerialName("pin_2")
    val pin2: Int,

    @SerialName("pin_0")
    val pin0: Int,

    @SerialName("pin_14")
    val pin14: Int,

    @SerialName("pin_15")
    val pin15: Int,

    @SerialName("pin_5")
    val pin5: Int,

    @SerialName("pin_4")
    val pin4: Int
)