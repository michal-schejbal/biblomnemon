package com.ginoskos.biblomnemon.core

import com.example.nbaplayers.app.logger.ILogger
import org.koin.java.KoinJavaComponent.inject
import retrofit2.HttpException
import retrofit2.Response
import kotlin.coroutines.cancellation.CancellationException


/**
 * Executes a suspendable block and wraps the result in a [Result], while correctly handling coroutine cancellation.
 *
 * This wrapper ensures that `CancellationException` (used internally by Kotlin coroutines to cancel jobs) is rethrown
 * instead of being wrapped as a failure. All other exceptions are caught and returned as a failed [Result].
 *
 * @param block The suspendable block to execute.
 * @return A [Result] wrapping the success or failure of the block.
 */
suspend inline fun <T> runCatchingCancellable(crossinline block: suspend () -> T): Result<T> {
    val logger: ILogger by inject(ILogger::class.java)
    return try {
        logger.d("runCatchingCancellable: Starting block execution.")
        val value = block()
        logger.d("runCatchingCancellable: Success.")
        Result.success(value)
    } catch (e: Throwable) {
        if (e is CancellationException) {
            logger.w("runCatchingCancellable: Cancelled.")
            throw e
        }
        logger.e(e, "runCatchingCancellable: Failure.")
        Result.failure(e)
    }
}

/**
 * Executes a suspendable Retrofit network call and wraps the result in a [Result].
 *
 * Uses [runCatchingCancellable] to safely handle cancellation and exceptions.
 * If the HTTP response is successful, returns the body as a success.
 * If the body is null or the response fails, throws an exception.
 *
 * @param block The suspendable Retrofit call to be executed.
 * @return A [Result] containing the body of the response if successful, or a failure otherwise.
 * @throws HttpException If the response is not successful.
 * @throws Exception If the response body is null.
 */
suspend inline fun <T> safeApiCall(crossinline block: suspend () -> Response<T>): Result<T> {
    val logger: ILogger by inject(ILogger::class.java)
    return runCatchingCancellable {
        logger.d("Performing API call...")
        val response = block()
        logger.d("API call completed with code: %d", response.code())
        if (response.isSuccessful) {
            response.body() ?: throw kotlin.Exception("Empty response")
        } else {
            logger.e(null, "API call failed with error body: %s", response.errorBody()?.string())
            throw HttpException(response)
        }
    }
}