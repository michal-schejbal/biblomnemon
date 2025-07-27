package com.ginoskos.biblomnemon.core.integrity

interface IIntegrityManager {
    suspend fun check(): IntegrityVerdict
}