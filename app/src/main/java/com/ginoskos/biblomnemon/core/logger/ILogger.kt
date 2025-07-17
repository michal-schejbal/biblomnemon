package com.example.nbaplayers.app.logger

import android.util.Log

/**
 * Enum representing different levels of logging severity.
 *
 * @property priority The Android log priority associated with the level.
 */
enum class LogLevel(val priority: Int) {
    VERBOSE(Log.VERBOSE),
    DEBUG(Log.DEBUG),
    INFO(Log.INFO),
    WARN(Log.WARN),
    ERROR(Log.ERROR),
    ASSERT(Log.ASSERT),
    WTF(Log.ASSERT)
}

/**
 * Logging interface for abstracting platform logging and formatting.
 */
interface ILogger {
    fun verbose(message: String, vararg args: Any?)
    fun debug(message: String, vararg args: Any?)
    fun info(message: String, vararg args: Any?)
    fun warn(message: String, vararg args: Any?)
    fun error(throwable: Throwable? = null, message: String, vararg args: Any?)
    fun assert(throwable: Throwable? = null, message: String, vararg args: Any?)
    fun log(level: LogLevel, message: String, vararg args: Any?)

    /** Alias functions */
    fun v(message: String, vararg args: Any?) = verbose(message, *args)
    fun d(message: String, vararg args: Any?) = debug(message, *args)
    fun i(message: String, vararg args: Any?) = info(message, *args)
    fun w(message: String, vararg args: Any?) = warn(message, *args)
    fun e(throwable: Throwable? = null, message: String, vararg args: Any?) = error(throwable, message, *args)
    fun wtf(throwable: Throwable? = null, message: String, vararg args: Any?) = assert(throwable, message, *args)
}