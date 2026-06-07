package com.preetanshu.postlyandroidxml.models

import kotlinx.serialization.Serializable

@Serializable
data class PostModel(
    val id: Int? = null,
    val userId: Int? = null,
    val title: String? = null,
    val body: String? = null
)
