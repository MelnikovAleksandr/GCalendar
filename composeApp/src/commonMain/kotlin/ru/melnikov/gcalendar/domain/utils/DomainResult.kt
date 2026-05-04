package ru.melnikov.gcalendar.domain.utils

sealed class DomainResult<out T> {
    data class Success<T>(val data: T) : DomainResult<T>()
    data class Error(val error: DomainError) : DomainResult<Nothing>()
}

sealed class DomainError(val message: String) {
    data object NoInternet : DomainError("No internet connection. Please check your network.")
    data object ServerError : DomainError("Server error. Please try again later.")
    data object Timeout : DomainError("Request timed out. Please try again.")
    data object Unauthorized : DomainError("Session expired. Please log in again.")
    data object DatabaseError : DomainError("Failed to save data locally.")
    data object NotFound : DomainError("The requested item was not found.")
    data class ValidationError(val errorMessage: String) : DomainError(errorMessage)
    data class Unknown(val errorMessage: String) : DomainError(errorMessage)
}

inline fun <T, R> DomainResult<T>.map(transform: (T) -> R): DomainResult<R> = when (this) {
    is DomainResult.Success -> DomainResult.Success(transform(data))
    is DomainResult.Error -> this
}

inline fun <T> DomainResult<T>.onSuccess(action: (T) -> Unit): DomainResult<T> {
    if (this is DomainResult.Success) action(data)
    return this
}

inline fun <T> DomainResult<T>.onError(action: (DomainError) -> Unit): DomainResult<T> {
    if (this is DomainResult.Error) action(error)
    return this
}

fun <T> DomainResult<T>.getOrNull(): T? = when (this) {
    is DomainResult.Success -> data
    is DomainResult.Error -> null
}

fun <T> DomainResult<T>.getOrDefault(default: T): T = when (this) {
    is DomainResult.Success -> data
    is DomainResult.Error -> default
}