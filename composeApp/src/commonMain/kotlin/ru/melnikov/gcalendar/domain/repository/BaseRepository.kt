package ru.melnikov.gcalendar.domain.repository

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import ru.melnikov.gcalendar.common.AppLogger
import ru.melnikov.gcalendar.domain.utils.DomainError
import ru.melnikov.gcalendar.domain.utils.DomainResult
import ru.melnikov.gcalendar.domain.utils.toDomainError

abstract class BaseRepository(
    protected val ioDispatcher: CoroutineDispatcher = Dispatchers.Default
) {

    protected open val tag: String get() = this::class.simpleName ?: "Repository"

    protected suspend inline fun <T> safeCall(
        operationName: String,
        crossinline block: suspend () -> T
    ): DomainResult<T> {
        return withContext(ioDispatcher) {
            try {
                AppLogger.d { "$tag: Starting $operationName" }
                val result = block()
                AppLogger.d { "$tag: $operationName completed successfully" }
                DomainResult.Success(result)
            } catch (e: Exception) {
                AppLogger.e(e) { "$tag: $operationName failed" }
                DomainResult.Error(e.toDomainError())
            }
        }
    }

    protected suspend inline fun <T> safeCallOrThrow(
        operationName: String,
        crossinline block: suspend () -> T
    ): T {
        return withContext(ioDispatcher) {
            try {
                AppLogger.d { "$tag: Starting $operationName" }
                val result = block()
                AppLogger.d { "$tag: $operationName completed successfully" }
                result
            } catch (e: Exception) {
                val errorMessage = "$operationName failed: ${e.message}"
                AppLogger.e(e) { "$tag: $errorMessage" }
                throw RepositoryException(errorMessage, e)
            }
        }
    }

    protected fun <T> safeFlow(
        flowName: String,
        defaultValue: T,
        flow: Flow<T>
    ): Flow<T> {
        return flow
            .catch { e ->
                AppLogger.e(e as? Exception ?: Exception(e)) { "$tag: $flowName error" }
                emit(defaultValue)
            }
            .flowOn(ioDispatcher)
    }

    protected fun logDebug(message: () -> String) {
        AppLogger.d { "$tag: ${message()}" }
    }

    protected fun logError(exception: Exception? = null, message: () -> String) {
        if (exception != null) {
            AppLogger.e(exception) { "$tag: ${message()}" }
        } else {
            AppLogger.e { "$tag: ${message()}" }
        }
    }

    protected fun logInfo(message: () -> String) {
        AppLogger.i { "$tag: ${message()}" }
    }
}

inline fun <T> Result<T>.toDomainResult(
    errorMapper: (Throwable) -> DomainError = { it.toDomainError() }
): DomainResult<T> {
    return fold(
        onSuccess = { DomainResult.Success(it) },
        onFailure = { DomainResult.Error(errorMapper(it)) }
    )
}
