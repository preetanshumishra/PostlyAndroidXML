package com.preetanshu.postlyandroidxml.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class LoginResponse(
    @SerialName("api_key")
    val apiKey: String
)
