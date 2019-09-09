package gsrv.klassenplaner.data.repository

import androidx.lifecycle.liveData
import androidx.lifecycle.map
import gsrv.klassenplaner.data.Result
import gsrv.klassenplaner.data.database.daos.ExamDao
import gsrv.klassenplaner.data.entities.Exam
import gsrv.klassenplaner.data.entities.Group
import gsrv.klassenplaner.data.network.ExamApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber

/**
 * Handles data operation and acts as a mediator between [ExamApi] and [ExamDao].
 */
class ExamRepository(
    private val examApi: ExamApi,
    private val examDao: ExamDao
) : BaseRepository<Exam> {

    override fun get(groups: List<Group>) = liveData<Result<List<Exam>>> {
        val disposable = emitSource(
            examDao.getAllForGroups(groups.map(Group::id)).map { oldData ->
                Result.Loading(oldData)
            }
        )

        try {
            refetch(groups)

            // Stop the previous emission to avoid dispatching the updated
            // exams as `loading` while we update the database
            disposable.dispose()

            emitSource(
                examDao.getAllForGroups(groups.map(Group::id)).map { newData ->
                    Result.Success(newData)
                }
            )
        } catch (exception: Exception) {
            Timber.e(exception)
            emitSource(
                examDao.getAllForGroups(groups.map(Group::id)).map { oldData ->
                    Result.Error(oldData, exception)
                }
            )
        }
    }

    override suspend fun get(itemId: Int): Exam = withContext(Dispatchers.IO) {
        examDao.get(itemId)
    }

    override suspend fun refetch(groups: List<Group>) {
        withContext(Dispatchers.IO) {
            groups.forEach { group ->
                val exams = examApi.getExams(group.id)
                examDao.updateAllForGroup(group.id, exams)
            }
        }
    }

    override suspend fun create(item: Exam) {
        withContext(Dispatchers.IO) {
            val newExam = examApi.createExam(item.groupId, item)
            examDao.insert(newExam)
        }
    }

    override suspend fun update(item: Exam) {
        withContext(Dispatchers.IO) {
            val newExam = examApi.updateExam(/*item.groupId, */item.id, item)
            examDao.update(newExam)
        }
    }

    override suspend fun delete(item: Exam) {
        withContext(Dispatchers.IO) {
            examApi.deleteExam(item.id)
            examDao.delete(item)
        }
    }
}
