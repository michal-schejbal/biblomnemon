package com.ginoskos.biblomnemon.data.storage.cloud.storage

import android.content.Context
import com.example.nbaplayers.app.logger.ILogger
import com.ginoskos.biblomnemon.core.auth.ITokenStorage
import com.ginoskos.biblomnemon.core.settings.ISettings
import com.ginoskos.biblomnemon.data.repositories.ILocalBooksRepository
import com.ginoskos.biblomnemon.data.repositories.ILocalCategoriesRepository
import com.ginoskos.biblomnemon.data.storage.cloud.auth.GoogleAuthManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.withContext
import org.koin.java.KoinJavaComponent.inject

class GoogleStorageManager(
    private val context: Context,
    private val auth: GoogleAuthManager,
    private val settings: ISettings,
    private val tokenStorage: ITokenStorage,
    private val logger: ILogger
) : ICloudStorageManager {
    private val booksRepository: ILocalBooksRepository by inject(ILocalBooksRepository::class.java)
    private val categoriesRepository: ILocalCategoriesRepository by inject(ILocalCategoriesRepository::class.java)

    override suspend fun upload(): Result<Unit> = runCatching {
        if (!auth.isSignedIn()) {
            error("User not signed in")
        }
        if (!auth.isAuthorized()) {
            error("User not authorized for Drive/Sheets")
        }

        // TODO token should be refreshed differently
        val scopes = listOf(
            "https://www.googleapis.com/auth/drive.file", // Also drive.appdata could be use if hidden app folder is needed
            "https://www.googleapis.com/auth/spreadsheets"
        )
        auth.authorizationRequest(scopes)

        val sheets = GoogleSheetsManager(
            context = context,
            tokenStorage = tokenStorage
        )

        val spreadsheetId = withContext(Dispatchers.IO) {
            var id = settings.getSpreadsheetId().firstOrNull()
            if (id.isNullOrBlank()) {
                val title = context.applicationInfo.labelRes.let {
                    if (it == 0) context.applicationInfo.name else context.getString(it)
                }
                id = sheets.create(title)
                    ?: error("Failed to create spreadsheet")
                settings.setSpreadsheetId(id)
                logger.i("Created spreadsheet: %s", id)
            } else {
                logger.d("Using existing spreadsheet: %s", id)
            }
            id
        }

        // Books Tab
        val books = booksRepository.fetch().getOrThrow()
        val bookRows = books.map { it.toRow() }
        sheets.update(spreadsheetId, BookSheet.TITLE, BookSheet.headers, bookRows)
        logger.d("Books exported: %d", bookRows.size)

        // Categories Tab
        val categories = categoriesRepository.fetch().getOrThrow()
        val categoryRows = categories.map { it.toRow() }
        sheets.update(spreadsheetId, CategorySheet.TITLE, CategorySheet.headers, categoryRows)
        logger.d("Categories exported: %d", categoryRows.size)

        // Book + Categories Relations Tab
        val relationRows = buildList {
            for (book in books) {
                val cats = categoriesRepository.fetchByBookId(book.id).getOrDefault(emptyList())
                cats.forEach { add(relationRow(book.id, it.id)) }
            }
        }
        sheets.update(spreadsheetId, RelationSheet.TITLE, RelationSheet.headers, relationRows)
        logger.d("Relations exported: %d", relationRows.size)
    }

    override suspend fun download(): Result<Unit> {
        return Result.failure(UnsupportedOperationException("Download not implemented"))
    }
}