package com.ginoskos.biblomnemon.core.integrity

data class IntegrityVerdict(
    val isTrusted: Boolean,
    val reasons: List<String> = emptyList()
)