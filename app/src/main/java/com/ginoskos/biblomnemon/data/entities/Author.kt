package com.ginoskos.biblomnemon.data.entities

import kotlinx.serialization.Serializable

@Serializable
data class Author(
    val name: String,
    val birthDate: String? = null,
    val deathDate: String? = null,
    val bio: String? = null,
    val imageUrl: String? = null
)
