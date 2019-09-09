package gsrv.klassenplaner.data.repository

import androidx.lifecycle.LiveDataScope
import androidx.lifecycle.liveData
import androidx.lifecycle.map
import gsrv.klassenplaner.data.NetworkBoundResult
import gsrv.klassenplaner.data.Result
import gsrv.klassenplaner.data.database.daos.GroupDao
import gsrv.klassenplaner.data.entities.Group
import gsrv.klassenplaner.data.network.GroupApi
import gsrv.klassenplaner.util.RateLimiter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import timber.log.Timber
import java.util.concurrent.TimeUnit

class GroupRepository(
    private val groupApi: GroupApi,
    private val groupDao: GroupDao
) {

    private val rateLimiter = RateLimiter<String>(1, TimeUnit.MINUTES)

    fun getGroups() = liveData<Result<List<Group>>> {
        val disposable = emitSource(
            groupDao.getAll().map { groups ->
                Result.Loading(groups)
            }
        )

        try {
            // Fetch new data
            val newData = mutableListOf<Group>()
            groupDao.getAllAsync().forEach { group ->
                try {
                    // Make sure to preserve the password
                    val newGroup = groupApi.getGroup(group.id)
                    newGroup.password = group.password

                    newData.add(newGroup)
                } catch (exception: HttpException) {
                    Timber.d(exception)

                    if (exception.code() == 404 || exception.code() == 410) {
                        Timber.w("Group ${group.id} was either not found or deleted server-side, deleting it client-side as well.")
                        groupDao.delete(group.id)
                    }
                }
            }

            // Stop the previous emission to avoid dispatching the updated
            // groups as `loading` while we update the database
            disposable.dispose()

            // Update database
            groupDao.update(newData)

            // Emit the data with success type
            emitSource(
                groupDao.getAll().map { groups ->
                    Result.Success(groups)
                }
            )
        } catch (exception: Exception) {
            Timber.e(exception)
            emitSource(
                groupDao.getAll().map { groups ->
                    Result.Error(groups, exception)
                }
            )
        }
    }

    fun getGroup(groupId: Int, forceFetch: Boolean = false) = object : NetworkBoundResult<Group>() {
        override fun loadFromDb() =
            groupDao.get(groupId)

        override fun shouldFetch(data: Group?) =
            data == null || rateLimiter.shouldFetch(data.id.toString()) || forceFetch

        override suspend fun createCall() =
            groupApi.getGroup(groupId)

        override suspend fun saveCallResult(data: Group) {
            groupDao.upsert(data)
        }

        override suspend fun onFetchFailed(liveDataScope: LiveDataScope<Result<Group>>, exception: Exception) {
            super.onFetchFailed(liveDataScope, exception)
        }

    }.asLiveData()

    suspend fun createGroup(group: Group) {
        withContext(Dispatchers.IO) {
            val newGroup = groupApi.createGroup(group)
            newGroup.password = "foobar"
            groupDao.insert(newGroup)
        }
    }

    suspend fun updateGroup(group: Group) {
        withContext(Dispatchers.IO) {
            val newGroup = groupApi.updateGroup(group.id, group)
            newGroup.password = group.password
            groupDao.update(newGroup)
        }
    }

    suspend fun deleteGroup(group: Group) {
        withContext(Dispatchers.IO) {
            groupApi.deleteGroup(group.id)
            groupDao.delete(group.id)
        }
    }

    suspend fun deleteGroupLocally(group: Group) {
        withContext(Dispatchers.IO) {
            groupDao.delete(group.id)
        }
    }

    suspend fun logout(group: Group) {
        group.password = null
        withContext(Dispatchers.IO) {
            groupDao.update(group)
        }
    }

    suspend fun login(group: Group) {
        withContext(Dispatchers.IO) {
            // Imaginary delay to simulate a network request checking the password
            // TODO: REMOVE THIS WHEN THE FUNCTION GETS AN ACTUAL IMPLEMENTATION
            delay(500)

            // TODO: Check whether password is actually valid and not just blindly assume so
            groupDao.update(group)
        }
    }
}
