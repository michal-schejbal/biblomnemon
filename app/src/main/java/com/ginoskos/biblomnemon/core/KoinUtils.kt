package com.ginoskos.biblomnemon.core

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.lifecycle.ViewModel
import org.koin.compose.getKoin
import org.koin.core.context.GlobalContext
import org.koin.core.qualifier.named
import org.koin.core.scope.Scope

@Composable
inline fun <reified T : ViewModel> koinSharedViewModel(): T {
    return remember { GlobalContext.get().get<T>() }
}

@Composable
fun koinScreenScope(identifier: String): Scope {
    val koin = getKoin()
    val id = identifier
    val qualifier = named(id)
    return remember(id) {
        koin.getScopeOrNull(id) ?: koin.createScope(id, qualifier)
    }
}