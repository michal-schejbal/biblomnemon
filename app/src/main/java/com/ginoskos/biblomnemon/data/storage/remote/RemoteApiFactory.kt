package com.ginoskos.biblomnemon.data.storage.remote

import com.ginoskos.biblomnemon.BuildConfig
import com.ginoskos.biblomnemon.data.storage.remote.google.GoogleBooksApi
import com.ginoskos.biblomnemon.data.storage.remote.openlibrary.OpenLibraryApi
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

class RemoteApiFactory {
    private val moshi: Moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

    private fun createRetrofit(baseUrl: String): Retrofit {
        val okHttpBuilder = OkHttpClient.Builder()

        if (BuildConfig.DEBUG) {
            val logging = HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            }
            okHttpBuilder.addInterceptor(logging)
        }

        val client = okHttpBuilder
            .build()

        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(client)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
    }

    fun createGoogleBooksApi(): GoogleBooksApi {
        return createRetrofit("https://www.googleapis.com/books/v1/")
            .create(GoogleBooksApi::class.java)
    }

    fun createOpenLibraryApi(): OpenLibraryApi {
        return createRetrofit("https://openlibrary.org/")
            .create(OpenLibraryApi::class.java)
    }
}
