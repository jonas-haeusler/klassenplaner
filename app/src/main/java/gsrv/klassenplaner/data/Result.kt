package gsrv.klassenplaner.data

sealed class Result<T>(open val data: T?) {
    data class Success<T>(override val data: T): Result<T>(data)
    data class Loading<T>(override val data: T?): Result<T>(data)
    data class Error<T>(override val data: T?, val exception: Exception): Result<T>(data)
}
