package com.ginoskos.biblomnemon.core.app

import com.example.nbaplayers.app.logger.ILogger
import com.example.nbaplayers.app.logger.TimberLogger
import com.example.nbaplayers.model.AppDispatcherProvider
import com.example.nbaplayers.model.IDispatcherProvider
import com.ginoskos.biblomnemon.core.Config
import com.ginoskos.biblomnemon.core.auth.ITokenStorage
import com.ginoskos.biblomnemon.core.auth.TokenStorage
import com.ginoskos.biblomnemon.core.scanner.IBarcodeScanner
import com.ginoskos.biblomnemon.core.scanner.MLKitBarcodeScanner
import com.ginoskos.biblomnemon.core.security.ICrypto
import com.ginoskos.biblomnemon.core.security.TinkCrypto
import com.ginoskos.biblomnemon.core.settings.ISettings
import com.ginoskos.biblomnemon.core.settings.Settings
import com.ginoskos.biblomnemon.data.repositories.IBooksRepository
import com.ginoskos.biblomnemon.data.repositories.ILocalBooksRepository
import com.ginoskos.biblomnemon.data.repositories.ILocalCategoriesRepository
import com.ginoskos.biblomnemon.data.repositories.ILocalReadingActivitiesRepository
import com.ginoskos.biblomnemon.data.repositories.LocalBooksRepository
import com.ginoskos.biblomnemon.data.repositories.LocalCategoriesRepository
import com.ginoskos.biblomnemon.data.repositories.LocalReadingActivitiesRepository
import com.ginoskos.biblomnemon.data.repositories.RemoteBooksRepository
import com.ginoskos.biblomnemon.data.storage.cloud.auth.GoogleAuthManager
import com.ginoskos.biblomnemon.data.storage.cloud.auth.GoogleAuthorizationBackend
import com.ginoskos.biblomnemon.data.storage.cloud.auth.GoogleAuthorizationLocal
import com.ginoskos.biblomnemon.data.storage.cloud.auth.ICloudAuthManager
import com.ginoskos.biblomnemon.data.storage.cloud.storage.GoogleStorageManager
import com.ginoskos.biblomnemon.data.storage.cloud.storage.ICloudStorageManager
import com.ginoskos.biblomnemon.data.storage.database.ApplicationDatabase
import com.ginoskos.biblomnemon.data.storage.database.DatabaseFactory
import com.ginoskos.biblomnemon.data.storage.remote.RemoteApiFactory
import com.ginoskos.biblomnemon.data.storage.remote.google.GoogleBooksApi
import com.ginoskos.biblomnemon.data.storage.remote.google.GoogleBooksSource
import com.ginoskos.biblomnemon.data.storage.remote.openlibrary.OpenLibraryApi
import com.ginoskos.biblomnemon.data.storage.remote.openlibrary.OpenLibrarySource
import com.ginoskos.biblomnemon.ui.screens.activity.ReadingActivityEditViewModel
import com.ginoskos.biblomnemon.ui.screens.activity.ReadingActivityTransferViewModel
import com.ginoskos.biblomnemon.ui.screens.activity.ReadingActivityViewModel
import com.ginoskos.biblomnemon.ui.screens.common.BookPickerViewModel
import com.ginoskos.biblomnemon.ui.screens.common.CategoryManagerViewModel
import com.ginoskos.biblomnemon.ui.screens.library.BookTransferViewModel
import com.ginoskos.biblomnemon.ui.screens.library.LibraryEditViewModel
import com.ginoskos.biblomnemon.ui.screens.library.LibraryViewModel
import com.ginoskos.biblomnemon.ui.screens.profile.ProfileViewModel
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
            // Settings
            single<ISettings> { Settings(androidContext()) }
            // Barcode scanner
            single<IBarcodeScanner> { MLKitBarcodeScanner(androidContext(), get()) }
            // Security
            single<ICrypto> { TinkCrypto(context = androidContext(), logger = get()) }
            single<ITokenStorage> {

                TokenStorage(androidContext(), get())
            }
        },
        module {
            single { DatabaseFactory().createRoom(androidContext(), ApplicationDatabase::class.java)}
            single { get<ApplicationDatabase>().bookDao() }
            single { get<ApplicationDatabase>().categoryDao() }
            single { get<ApplicationDatabase>().readingActivityDao() }

            single<ILocalBooksRepository> { LocalBooksRepository(get(), get(),get()) }
            single<ILocalCategoriesRepository> { LocalCategoriesRepository(get(), get()) }
            single<ILocalReadingActivitiesRepository> { LocalReadingActivitiesRepository(get(), get()) }
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
            single<ICloudAuthManager> { GoogleAuthManager(
                context = androidContext(),
                tokenStorage = get(),
                settings = get(),
                authorization = if (Config.GOOGLE_USE_BACKEND_AUTH) {
                    GoogleAuthorizationBackend(get()) }
                else {
                    GoogleAuthorizationLocal(get())
                },
                webClientId = Config.GOOGLE_CLIENT_ID
            ) }
            single<ICloudStorageManager> {
                GoogleStorageManager(
                    context = androidContext(),
                    auth = get<ICloudAuthManager>() as GoogleAuthManager,
                    settings = get(),
                    tokenStorage = get(),
                    logger = get()
                )
            }
        },
        module {
            single { BookTransferViewModel(get()) }
            single { ReadingActivityTransferViewModel(get()) }

            // Profile
            viewModel { ProfileViewModel(get(), get(), get(), get()) }

            // Search
            viewModel { SearchViewModel(get(), get()) }
            viewModel { SearchDetailViewModel(get(), get()) }

            // Library
            viewModel { LibraryViewModel(get(), get()) }
            viewModel { LibraryEditViewModel(get(), get(), get()) }
            viewModel { CategoryManagerViewModel(get(), get()) }

            // Scan
            viewModel { ScannerViewModel(get(), get()) }

            // Activity
            viewModel { ReadingActivityViewModel(get(), get()) }
            viewModel { ReadingActivityEditViewModel(get(), get()) }
            viewModel { BookPickerViewModel(get(), get()) }
        }
    )
}

