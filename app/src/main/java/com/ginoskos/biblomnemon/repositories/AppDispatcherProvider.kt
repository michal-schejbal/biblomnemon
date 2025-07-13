package com.example.nbaplayers.model

import kotlinx.coroutines.Dispatchers

/**
 * Production implementation of [IDispatcherProvider] that provides default coroutine dispatchers.
 *
 * Used to control coroutine execution context throughout the app.
 */
object AppDispatcherProvider : IDispatcherProvider {
    override val main = Dispatchers.Main
    override val io = Dispatchers.IO
    override val default = Dispatchers.Default
}