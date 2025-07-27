package com.ginoskos.biblomnemon.data.repositories.storage.remote.google

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface GoogleBooksApi {
    @GET("volumes")
    suspend fun search(@Query("q") query: String): Response<GoogleBooksResponse>
    @GET("volumes/{id}")
    suspend fun getById(@Path("id") id: String): Response<GoogleBookItem>
    @GET("volumes")
    suspend fun getByIsbn(@Query("q") query: String): Response<GoogleBooksResponse>
}
