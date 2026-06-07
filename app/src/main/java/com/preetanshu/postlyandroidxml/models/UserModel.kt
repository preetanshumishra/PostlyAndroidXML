package com.preetanshu.postlyandroidxml.models

import kotlinx.serialization.Serializable

@Serializable
data class UserModel(
    val id: Int? = null,
    val avatar: String? = null,
    val name: String? = null,
    val username: String? = null,
    val email: String? = null
)
