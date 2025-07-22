package com.ginoskos.biblomnemon.core.app

import com.example.nbaplayers.app.logger.ILogger
import com.example.nbaplayers.app.logger.TimberLogger
import com.example.nbaplayers.model.AppDispatcherProvider
import com.example.nbaplayers.model.IDispatcherProvider
import com.ginoskos.biblomnemon.repositories.books.BooksRepository
import com.ginoskos.biblomnemon.repositories.books.IBooksRepository
import com.ginoskos.biblomnemon.repositories.books.storage.remote.ApiFactory
import com.ginoskos.biblomnemon.repositories.books.storage.remote.IBooksRemoteSource
import com.ginoskos.biblomnemon.repositories.books.storage.remote.google.GoogleBooksApi
import com.ginoskos.biblomnemon.repositories.books.storage.remote.google.GoogleBooksSource
import com.ginoskos.biblomnemon.repositories.books.storage.remote.openlibrary.OpenLibraryApi
import com.ginoskos.biblomnemon.repositories.books.storage.remote.openlibrary.OpenLibrarySource
import com.ginoskos.biblomnemon.ui.screens.search.SearchViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

/**
 * Koin modules used to configure dependency injection throughout the app.
 *
 * Provides:
 * - Logging and dispatcher bindings
 * - Retrofit API setup
 *
 */
object Modules {
     val items = listOf(
         module {
             // Core dependencies
             single<ILogger> { TimberLogger }
             single<IDispatcherProvider> { AppDispatcherProvider }
         },
         module {
             factory<GoogleBooksApi> { ApiFactory().createGoogleBooksApi() }
             factory<OpenLibraryApi> { ApiFactory().createOpenLibraryApi() }

             factory { GoogleBooksSource(get<GoogleBooksApi>(), get()) }
             factory { OpenLibrarySource(get<OpenLibraryApi>(), get()) }

             factory<IBooksRemoteSource> { get<GoogleBooksSource>() }
             factory<IBooksRemoteSource> { get<OpenLibrarySource>() }

             factory<IBooksRepository> {
                 BooksRepository(
                     sources = listOf(
                         get<GoogleBooksSource>(),
                         get<OpenLibrarySource>()
                     ),
                     dispatcher = get()
                 )
             }
         },
         module {
             viewModel { SearchViewModel(get()) }
         }
     )
}