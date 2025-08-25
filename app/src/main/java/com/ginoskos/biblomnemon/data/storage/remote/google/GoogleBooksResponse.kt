package com.ginoskos.biblomnemon.data.storage.remote.google

import com.ginoskos.biblomnemon.data.entities.Author
import com.ginoskos.biblomnemon.data.entities.Book
import com.ginoskos.biblomnemon.data.entities.BookSource
import com.ginoskos.biblomnemon.data.repositories.forceHttps
import java.time.LocalDate
import java.time.Year
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

data class GoogleBooksResponse(
    val items: List<GoogleBookItem>?
)

data class GoogleBookItem(
    val id: String?,
    val volumeInfo: GoogleVolumeInfo?
)

data class GoogleVolumeInfo(
    val title: String?,
    val subtitle: String?,
    val authors: List<String>?,
    val industryIdentifiers: List<GoogleIndustryIdentifier>?,
    val imageLinks: GoogleImageLinks?,
    val language: String?,
    val publishedDate: String?,
    val publisher: String?,
    val description: String?,
    val pageCount: Int?,
    val categories: List<String>?
)

data class GoogleImageLinks(
    val smallThumbnail: String? = null,
    val thumbnail: String? = null,
    val small: String? = null,
    val medium: String? = null,
    val large: String? = null,
    val extraLarge: String? = null
)

data class GoogleIndustryIdentifier(
    val type: String?,
    val identifier: String?
)

fun GoogleBookItem.toDomain(): Book? {
    val info = volumeInfo ?: return null
    return Book(
        id = this.id ?: "",
        source = BookSource.GOOGLE,
        title = listOfNotNull(info.title, info.subtitle?.takeIf { it.isNotBlank() })
            .joinToString(separator = ": "),
        description = info.description,
        authors = info.authors?.map { Author(name = it) },
        isbn = info.industryIdentifiers?.lastOrNull() { it.type?.startsWith("ISBN") == true }?.identifier,
        language = info.language,
        covers = buildList {
            info.imageLinks?.small?.let { add(it.forceHttps()) } ?:
                info.imageLinks?.thumbnail?.let { add(it.forceHttps()) }
            info.imageLinks?.medium?.let { add(it.forceHttps()) }
            info.imageLinks?.large?.let { add(it.forceHttps()) }
        }.ifEmpty { null },
        publishYear = info.extractPublishYear(),
        publisher = info.publisher,
        pageCount = info.pageCount
    )
}

fun GoogleVolumeInfo.extractPublishYear(): Int? {
    val date = publishedDate?.trim()
    if (date.isNullOrEmpty()) {
        return null
    }
    return try {
        LocalDate.parse(date, DateTimeFormatter.ISO_LOCAL_DATE).year
    } catch (_: DateTimeParseException) {
        try {
            YearMonth.parse(date, DateTimeFormatter.ofPattern("yyyy-MM")).year
        } catch (_: DateTimeParseException) {
            try {
                Year.parse(date, DateTimeFormatter.ofPattern("yyyy")).value
            } catch (_: DateTimeParseException) {
                null
            }
        }
    }
}