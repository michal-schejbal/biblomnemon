package com.ginoskos.biblomnemon.core.app

import com.example.nbaplayers.app.logger.ILogger
import com.example.nbaplayers.app.logger.TimberLogger
import com.example.nbaplayers.model.AppDispatcherProvider
import com.example.nbaplayers.model.IDispatcherProvider
import com.ginoskos.biblomnemon.repositories.books.BooksRepository
import com.ginoskos.biblomnemon.repositories.books.IBooksRepository
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
             factory<IBooksRepository> { BooksRepository(get()) }
         },
         module {
             viewModel { SearchViewModel(get()) }
         }
     )
}