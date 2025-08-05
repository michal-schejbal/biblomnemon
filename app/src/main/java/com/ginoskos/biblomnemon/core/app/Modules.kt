package com.ginoskos.biblomnemon.core.app

import com.example.nbaplayers.app.logger.ILogger
import com.example.nbaplayers.app.logger.TimberLogger
import com.example.nbaplayers.model.AppDispatcherProvider
import com.example.nbaplayers.model.IDispatcherProvider
import com.ginoskos.biblomnemon.core.scanner.IBarcodeScanner
import com.ginoskos.biblomnemon.core.scanner.MLKitBarcodeScanner
import com.ginoskos.biblomnemon.data.repositories.IBooksRepository
import com.ginoskos.biblomnemon.data.repositories.ILocalBooksRepository
import com.ginoskos.biblomnemon.data.repositories.ILocalCategoriesRepository
import com.ginoskos.biblomnemon.data.repositories.LocalBooksRepository
import com.ginoskos.biblomnemon.data.repositories.LocalCategoriesRepository
import com.ginoskos.biblomnemon.data.repositories.RemoteBooksRepository
import com.ginoskos.biblomnemon.data.repositories.storage.database.ApplicationDatabase
import com.ginoskos.biblomnemon.data.repositories.storage.database.DatabaseFactory
import com.ginoskos.biblomnemon.data.repositories.storage.remote.RemoteApiFactory
import com.ginoskos.biblomnemon.data.repositories.storage.remote.google.GoogleBooksApi
import com.ginoskos.biblomnemon.data.repositories.storage.remote.google.GoogleBooksSource
import com.ginoskos.biblomnemon.data.repositories.storage.remote.openlibrary.OpenLibraryApi
import com.ginoskos.biblomnemon.data.repositories.storage.remote.openlibrary.OpenLibrarySource
import com.ginoskos.biblomnemon.ui.screens.library.BookTransferViewModel
import com.ginoskos.biblomnemon.ui.screens.library.CategoryManagerViewModel
import com.ginoskos.biblomnemon.ui.screens.library.LibraryEditViewModel
import com.ginoskos.biblomnemon.ui.screens.library.LibraryViewModel
import com.ginoskos.biblomnemon.ui.screens.scanner.ScannerViewModel
import com.ginoskos.biblomnemon.ui.screens.search.SearchDetailViewModel
import com.ginoskos.biblomnemon.ui.screens.search.SearchViewModel
import org.koin.android.ext.koin.androidContext
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
            // Barcode scanner
            single<IBarcodeScanner> { MLKitBarcodeScanner(androidContext(), get()) }
        },
        module {
            single { DatabaseFactory().createRoom(androidContext(), ApplicationDatabase::class.java)}
            single { get<ApplicationDatabase>().bookDao() }
            single { get<ApplicationDatabase>().categoryDao() }

            single<ILocalBooksRepository> { LocalBooksRepository(get(), get()) }
            single<ILocalCategoriesRepository> { LocalCategoriesRepository(get(), get()) }
        },
        module {
            factory<GoogleBooksApi> { RemoteApiFactory().createGoogleBooksApi() }
            factory<OpenLibraryApi> { RemoteApiFactory().createOpenLibraryApi() }

            factory { GoogleBooksSource(get<GoogleBooksApi>(), get()) }
            factory { OpenLibrarySource(get<OpenLibraryApi>(), get()) }

            factory<IBooksRepository> { get<GoogleBooksSource>() }
            factory<IBooksRepository> { get<OpenLibrarySource>() }

            single<IBooksRepository> {
                RemoteBooksRepository(
                    sources = listOf(
                        get<GoogleBooksSource>(),
                        get<OpenLibrarySource>()
                    ),
                    dispatcher = get()
                )
            }
        },
        module {
            single { BookTransferViewModel(get()) }

            // Search
            viewModel { SearchViewModel(get(), get()) }
            viewModel { SearchDetailViewModel(get(), get()) }

            // Library
            viewModel { LibraryViewModel(get(), get()) }
            viewModel { LibraryEditViewModel(get(), get()) }
            viewModel { CategoryManagerViewModel(get(), get()) }

            // Scan
            viewModel { ScannerViewModel(get(), get()) }
        }
    )
}

