package com.ginoskos.biblomnemon.data.entities

import kotlinx.serialization.Serializable

@Serializable
data class Category(
    val id: Long? = null,
    val title: String? = null,
    val created: Long? = null,
    val updated: Long? = null
)