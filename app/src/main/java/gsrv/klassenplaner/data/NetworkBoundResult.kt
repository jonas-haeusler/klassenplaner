package gsrv.klassenplaner.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.LiveDataScope
import androidx.lifecycle.liveData
import androidx.lifecycle.map
import timber.log.Timber

/**
 * A generic class that can provide a result backed by both the sqlite database and the network.
 */
abstract class NetworkBoundResult<T> {

    /**
     * Returns a [Result] wrapped into a [LiveData] object.
     */
    fun asLiveData() = liveData<Result<T>> {
        // Load old data from local database and emit with loading type
        val dbSource = loadFromDb()
        val disposable = emitSource(
            dbSource.map { oldData ->
                Result.Loading(oldData)
            }
        )

        try {
            // Stop the previous emission to avoid dispatching the updated
            // data as `loading` while we update the database
            disposable.dispose()

            // Fetch new data and update the database if necessary
            if (shouldFetch(dbSource.value)) {
                val newData = createCall()
                saveCallResult(newData)
            }

            // Re-establish the emission with success type using
            // the data stored inside the database
            emitSource(
                loadFromDb().map { newData ->
                    Result.Success(newData)
                }
            )
        } catch (exception: Exception) {
            Timber.d(exception)

            // Any call to `emit` disposes the previous one automatically so we don't
            // need to dispose it here as we didn't get an updated value.
            onFetchFailed(this, exception)
        }
    }

    /**
     * Provides the data stored locally to:
     *
     * 1. provide cached data while the network request is executing
     *
     * 2. provide cached data if [shouldFetch] returns false or the
     * network request failed
     *
     * 3. retrieve the data saved by [saveCallResult] so we can provide
     * a single source of truth (i.e a database, instead of relying on
     * both, the database and the network by just dispatching the network
     * result into the [LiveData] provided by [asLiveData])
     */
    protected abstract fun loadFromDb(): LiveData<T>

    /**
     * Whether new data should be fetched or the existing [data]
     * should be used again.
     */
    protected abstract fun shouldFetch(data: T?): Boolean

    /**
     * The network call to be executed.
     */
    protected abstract suspend fun createCall(): T

    /**
     * Provides the [data] returned by the network request. Use this
     * to save persistently i.e into a database.
     */
    protected abstract suspend fun saveCallResult(data: T)

    /**
     * Override this method to provide a customized failure handling.
     * Use the [liveDataScope] to emit your results and [exception]
     * to know what caused the failure.
     *
     * By default this function emits a [Result.Error] with the data
     * provided by [loadFromDb] and the thrown [exception].
     */
    protected open suspend fun onFetchFailed(
        liveDataScope: LiveDataScope<Result<T>>,
        exception: Exception
    ) {
        liveDataScope.emitSource(
            loadFromDb().map { oldData ->
                Result.Error(oldData, exception)
            }
        )
    }

}
