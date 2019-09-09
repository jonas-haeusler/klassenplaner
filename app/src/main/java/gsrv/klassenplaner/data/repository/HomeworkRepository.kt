package gsrv.klassenplaner.data.repository

import androidx.lifecycle.liveData
import androidx.lifecycle.map
import gsrv.klassenplaner.data.Result
import gsrv.klassenplaner.data.database.daos.HomeworkDao
import gsrv.klassenplaner.data.entities.Group
import gsrv.klassenplaner.data.entities.Homework
import gsrv.klassenplaner.data.network.HomeworkApi
import gsrv.klassenplaner.util.RateLimiter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.util.concurrent.TimeUnit

/**
 * Handles data operation and acts as a mediator between [HomeworkApi] and [HomeworkDao].
 */
class HomeworkRepository(
    private val homeworkApi: HomeworkApi,
    private val homeworkDao: HomeworkDao
) : BaseRepository<Homework> {

    private val rateLimiter = RateLimiter<String>(1, TimeUnit.MINUTES)

    override fun get(groups: List<Group>) = liveData<Result<List<Homework>>> {
        val disposable = emitSource(
            homeworkDao.getAllForGroups(groups.map(Group::id)).map { oldData ->
                Result.Loading(oldData)
            }
        )

        try {
            refetch(groups)

            // Stop the previous emission to avoid dispatching the updated
            // homework as `loading` while we update the database
            disposable.dispose()

            emitSource(
                homeworkDao.getAllForGroups(groups.map(Group::id)).map { newData ->
                    Result.Success(newData)
                }
            )
        } catch (exception: Exception) {
            Timber.e(exception)
            rateLimiter.dropAll()
            emitSource(
                homeworkDao.getAllForGroups(groups.map(Group::id)).map { oldData ->
                    Result.Error(oldData, exception)
                }
            )
        }
    }

    override suspend fun get(itemId: Int): Homework = withContext(Dispatchers.IO) {
        homeworkDao.get(itemId)
    }

    override suspend fun refetch(groups: List<Group>) {
        withContext(Dispatchers.IO) {
            groups.forEach { group ->
                val homework = homeworkApi.getHomework(group.id)
                homeworkDao.updateAllForGroup(group.id, homework)
            }
        }
    }

    override suspend fun create(item: Homework) {
        withContext(Dispatchers.IO) {
            val newHomework = homeworkApi.createHomework(item.groupId, item)
            homeworkDao.insert(newHomework)
        }
    }

    override suspend fun update(item: Homework) {
        withContext(Dispatchers.IO) {
            val newHomework = homeworkApi.updateHomework(/*item.groupId, */item.id, item)
            newHomework.completed = item.completed

            homeworkDao.update(newHomework)
        }
    }

    override suspend fun delete(item: Homework) {
        withContext(Dispatchers.IO) {
            homeworkApi.deleteHomework(item.id)
            homeworkDao.delete(item)
        }
    }

    /**
     * Updates the database entry for the given [item] without pushing it the the server.
     * Use this to update the completed status of homework.
     */
    suspend fun updateLocally(item: Homework) {
        withContext(Dispatchers.IO) {
            homeworkDao.update(item)
        }
    }
}
