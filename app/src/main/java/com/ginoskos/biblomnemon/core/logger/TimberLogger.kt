package com.example.nbaplayers.app.logger

import timber.log.Timber

/**
 * Implementation of [ILogger] that delegates logging to the Timber library.
 *
 * Automatically tags logs with the calling class and line number for easier traceability.
 *
 * This logger is initialized with a custom [Timber.DebugTree] that formats log tags in the format `ClassName:LineNumber`.
 */
object TimberLogger : ILogger {
    init {
        Timber.plant(object : Timber.DebugTree() {
            override fun createStackElementTag(element: StackTraceElement): String {
                val className = element.className.substringAfterLast('.').substringBefore('$')
                return "$className:${element.lineNumber}"
            }

            override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
                val stackElement = Throwable().stackTrace.firstOrNull {
                    !it.className.contains(Timber::class.java.simpleName) &&
                    !it.className.contains(ILogger::class.java.simpleName)
                }

                val desiredTag = stackElement?.let {
                    "${it.className.substringAfterLast('.')}:${it.lineNumber}"
                } ?: tag ?: "unknown"

                super.log(priority, desiredTag, message, t)
            }
        })
    }

    override fun verbose(message: String, vararg args: Any?) {
        Timber.v(message, *args)
    }

    override fun debug(message: String, vararg args: Any?) {
        Timber.d(message, *args)
    }

    override fun info(message: String, vararg args: Any?) {
        Timber.i(message, *args)
    }

    override fun warn(message: String, vararg args: Any?) {
        Timber.w(message, *args)
    }

    override fun error(throwable: Throwable?, message: String, vararg args: Any?) {
        Timber.e(throwable, message, *args)
    }

    override fun assert(throwable: Throwable?, message: String, vararg args: Any?) {
        Timber.wtf(throwable, message, *args)
    }

    override fun log(level: LogLevel, message: String, vararg args: Any?) {
        when (level) {
            LogLevel.VERBOSE -> verbose(message, *args)
            LogLevel.DEBUG -> debug(message, *args)
            LogLevel.INFO -> info(message, *args)
            LogLevel.WARN -> warn(message, *args)
            LogLevel.ERROR -> error(null, message, *args)
            LogLevel.ASSERT, LogLevel.WTF -> assert(null, message, *args)
        }
    }
}