package ru.melnikov.gcalendar.domain.utils

import ru.melnikov.gcalendar.data.remote.error.DataError
import ru.melnikov.gcalendar.data.store.StoreException
import ru.melnikov.gcalendar.domain.repository.RepositoryException

object ErrorMapper {

    fun mapToDomainError(throwable: Throwable): DomainError {
        return when (throwable) {
            is RepositoryException -> mapRepositoryException(throwable)
            is StoreException -> mapStoreException(throwable)
            is EventValidationException -> DomainError.ValidationError(throwable.message ?: "Validation failed")
            else -> mapGenericException(throwable)
        }
    }

    private fun mapRepositoryException(exception: RepositoryException): DomainError {
        val cause = exception.cause
        return when {
            cause is StoreException -> mapStoreException(cause)
            exception.message.contains("internet", ignoreCase = true) -> DomainError.NoInternet
            exception.message.contains("timeout", ignoreCase = true) -> DomainError.Timeout
            exception.message.contains("unauthorized", ignoreCase = true) -> DomainError.Unauthorized
            exception.message.contains("server", ignoreCase = true) -> DomainError.ServerError
            else -> DomainError.Unknown(exception.message)
        }
    }

    private fun mapStoreException(exception: StoreException): DomainError {
        val message = exception.message ?: "Store operation failed"
        return when {
            message.contains("NO_INTERNET", ignoreCase = true) -> DomainError.NoInternet
            message.contains("TIMEOUT", ignoreCase = true) -> DomainError.Timeout
            message.contains("UNAUTHORIZED", ignoreCase = true) -> DomainError.Unauthorized
            message.contains("SERVER", ignoreCase = true) -> DomainError.ServerError
            message.contains("not found", ignoreCase = true) -> DomainError.NotFound
            else -> DomainError.Unknown(message)
        }
    }

    private fun mapGenericException(throwable: Throwable): DomainError {
        val message = throwable.message ?: "An unexpected error occurred"
        return when {
            message.contains("network", ignoreCase = true) -> DomainError.NoInternet
            message.contains("timeout", ignoreCase = true) -> DomainError.Timeout
            message.contains("database", ignoreCase = true) -> DomainError.DatabaseError
            else -> DomainError.Unknown(message)
        }
    }
}

fun DataError.toDomainError(): DomainError = when (this) {
    DataError.Network.NO_INTERNET -> DomainError.NoInternet
    DataError.Network.SERVER_ERROR -> DomainError.ServerError
    DataError.Network.REQUEST_TIMEOUT -> DomainError.Timeout
    DataError.Network.UNAUTHORIZED -> DomainError.Unauthorized
    DataError.Network.CONFLICT -> DomainError.Unknown("A conflict occurred. Please try again.")
    DataError.Network.SERIALIZATION -> DomainError.Unknown("Failed to process server response.")
    DataError.Network.PAYLOAD_TOO_LARGE -> DomainError.Unknown("Data too large to upload.")
    DataError.Network.UNKNOWN -> DomainError.Unknown("An unexpected error occurred.")
    DataError.Local.DISK_FULL -> DomainError.DatabaseError
}

fun Throwable.toDomainError(): DomainError = ErrorMapper.mapToDomainError(this)

inline fun <T> runCatchingToDomainResult(block: () -> T): DomainResult<T> {
    return try {
        DomainResult.Success(block())
    } catch (e: Exception) {
        DomainResult.Error(e.toDomainError())
    }
}

suspend inline fun <T> runSuspendCatchingToDomainResult(
    crossinline block: suspend () -> T
): DomainResult<T> {
    return try {
        DomainResult.Success(block())
    } catch (e: Exception) {
        DomainResult.Error(e.toDomainError())
    }
}