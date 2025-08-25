package com.ginoskos.biblomnemon.data.storage.remote.openlibrary

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface OpenLibraryApi {
    @GET("search.json")
    suspend fun search(@Query("q") query: String): Response<OpenLibraryResponse>
    @GET("works/{id}.json")
    suspend fun getById(@Path("id") id: String): Response<OpenLibraryDoc>
    @GET("search.json")
    suspend fun getByIsbn(@Query("isbn") isbn: String): Response<OpenLibraryResponse>
}