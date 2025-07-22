package com.ginoskos.biblomnemon.repositories.books.storage.remote.openlibrary

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface OpenLibraryApi {
    @GET("search.json")
    suspend fun search(@Query("q") query: String): Response<OpenLibraryResponse>
}