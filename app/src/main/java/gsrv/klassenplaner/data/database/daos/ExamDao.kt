package gsrv.klassenplaner.data.database.daos

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import gsrv.klassenplaner.data.entities.Exam
import gsrv.klassenplaner.data.entities.Homework

@Dao
abstract class ExamDao : BaseDao<Exam>() {

    @Query("SELECT * FROM exams WHERE id = :id ORDER BY date")
    abstract suspend fun get(id: Int): Exam

    @Query("SELECT * FROM exams ORDER BY date")
    abstract fun getAll(): LiveData<List<Exam>>

    @Query("SELECT * FROM exams WHERE groupId IN (:groupIds) ORDER BY date")
    abstract fun getAllForGroups(groupIds: List<Int>): LiveData<List<Exam>>

    @Query("DELETE FROM exams")
    abstract suspend fun deleteAll()

    @Query("DELETE FROM exams WHERE groupId = :groupId")
    abstract suspend fun deleteAllForGroup(groupId: Int)

    @Transaction
    open suspend fun updateAll(exam: List<Exam>) {
        deleteAll()
        insert(exam)
    }

    @Transaction
    open suspend fun updateAllForGroup(groupId: Int, exam: List<Exam>) {
        deleteAllForGroup(groupId)
        insert(exam)
    }
}
