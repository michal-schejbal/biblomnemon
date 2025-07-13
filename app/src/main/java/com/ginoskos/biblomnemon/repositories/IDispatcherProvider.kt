package com.example.nbaplayers.model

import kotlinx.coroutines.CoroutineDispatcher

/**
 * Abstraction layer for providing coroutine dispatchers.
 *
 * Allows for better testability and flexibility by decoupling coroutine context usage from implementation.
 */
interface IDispatcherProvider {
    val main: CoroutineDispatcher
    val io: CoroutineDispatcher
    val default: CoroutineDispatcher
}