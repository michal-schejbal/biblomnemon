package com.ginoskos.biblomnemon.data.storage.cloud.storage

import android.content.Context
import com.ginoskos.biblomnemon.core.auth.ITokenStorage
import com.google.api.client.http.HttpRequestInitializer
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.client.json.gson.GsonFactory
import com.google.api.services.sheets.v4.Sheets
import com.google.api.services.sheets.v4.model.AddSheetRequest
import com.google.api.services.sheets.v4.model.BatchUpdateSpreadsheetRequest
import com.google.api.services.sheets.v4.model.ClearValuesRequest
import com.google.api.services.sheets.v4.model.Request
import com.google.api.services.sheets.v4.model.SheetProperties
import com.google.api.services.sheets.v4.model.Spreadsheet
import com.google.api.services.sheets.v4.model.SpreadsheetProperties
import com.google.api.services.sheets.v4.model.ValueRange
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class GoogleSheetsManager(
    private val context: Context,
    private val tokenStorage: ITokenStorage,
) {
    enum class ValueInputOption(val value: String) {
        RAW("RAW")
    }

    suspend fun service(): Sheets? {
        val tokens = tokenStorage.read() ?: return null
        val transport = NetHttpTransport()
        val json = GsonFactory.getDefaultInstance()

        val credential = HttpRequestInitializer { request ->
            request.headers.authorization = "Bearer ${tokens.accessToken}"
            request.connectTimeout = 30_000
            request.readTimeout = 30_000
        }

        val title = context.applicationInfo.labelRes.let {
            if (it == 0) context.applicationInfo.name else context.getString(it)
        }

        return Sheets.Builder(transport, json, credential)
            .setApplicationName(title)
            .build()
    }

    suspend fun create(title: String): String? = withContext(Dispatchers.IO) {
        val svc = service() ?: return@withContext null
        val sheet = Spreadsheet().setProperties(SpreadsheetProperties().setTitle(title))
        val created = svc.Spreadsheets().create(sheet).execute()
        created.spreadsheetId
    }

    private suspend fun exists(spreadsheetId: String, title: String) = withContext(Dispatchers.IO) {
        val svc = service() ?: return@withContext
        val ss = svc.Spreadsheets().get(spreadsheetId).setIncludeGridData(false).execute()
        val exists = ss.sheets?.any { it.properties?.title == title } == true
        if (!exists) {
            val add = Request().setAddSheet(AddSheetRequest().setProperties(SheetProperties().setTitle(title)))
            svc.Spreadsheets().batchUpdate(
                spreadsheetId,
                BatchUpdateSpreadsheetRequest().setRequests(listOf(add))
            ).execute()
        }
    }

    suspend fun update(
        spreadsheetId: String,
        sheetTitle: String,
        headers: List<String>,
        rows: List<List<Any>>
    ) = withContext(Dispatchers.IO) {
        val svc = service() ?: return@withContext
        exists(spreadsheetId, sheetTitle)

        // Clear
        svc.Spreadsheets().values().clear(
            spreadsheetId, "$sheetTitle!A:Z",
            ClearValuesRequest()
        ).execute()

        // Write headers + rows
        val values = buildList(rows.size + 1) {
            add(headers)
            addAll(rows)
        }
        val body = ValueRange().setValues(values)
        svc.Spreadsheets().values()
            .update(spreadsheetId, "$sheetTitle!A1", body)
            .setValueInputOption(ValueInputOption.RAW.value)
            .execute()
    }
}