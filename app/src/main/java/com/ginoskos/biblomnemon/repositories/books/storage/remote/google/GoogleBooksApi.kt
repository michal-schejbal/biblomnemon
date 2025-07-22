package com.ginoskos.biblomnemon.repositories.books.storage.remote.google

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface GoogleBooksApi {
    @GET("volumes")
    suspend fun search(@Query("q") query: String): Response<GoogleBooksResponse>
}
