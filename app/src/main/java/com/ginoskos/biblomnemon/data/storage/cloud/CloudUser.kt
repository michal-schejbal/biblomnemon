package com.ginoskos.biblomnemon.data.storage.cloud

import kotlinx.serialization.Serializable

@Serializable
data class CloudUser(
    val id: String? = null,
    val name: String? = null,
    val email: String? = null,
    val avatar: String? = null
)