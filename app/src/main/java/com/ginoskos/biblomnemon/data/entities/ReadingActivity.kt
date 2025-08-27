package com.ginoskos.biblomnemon.data.entities

import kotlinx.serialization.Serializable

@Serializable
data class ReadingActivity(
    val id: Long? = null,
    val book: Book? = null,
    val title: String? = null,
    val description: String? = null,
    val started: Long,
    val ended: Long? = null,
    val pagesRead: Int? = null,
    val created: Long? = System.currentTimeMillis(),
    val updated: Long? = System.currentTimeMillis()
) {
    val duration: Long?
        get() {
            val end = ended ?: System.currentTimeMillis()
            return (end - started).takeIf { it >= 0 }
        }

    fun toDurationReadable(): String {
        val ms = duration ?: return ""
        var secs = ms / 1000
        val days = secs / 86_400; secs %= 86_400
        val hours = secs / 3_600; secs %= 3_600
        val minutes = secs / 60; val seconds = secs % 60

        return buildList {
            if (days > 0) add("${days}d")
            if (hours > 0) add("${hours}h")
            if (minutes > 0) add("${minutes}m")
            if (seconds > 0L || isEmpty()) add("${seconds}s") // show 0s if all above are zero
        }.joinToString(" ")
    }
}


