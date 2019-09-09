package gsrv.klassenplaner.data.database.daos

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import gsrv.klassenplaner.data.entities.Homework
import java.util.Calendar

@Dao
abstract class HomeworkDao : BaseDao<Homework>() {

    @Query("SELECT * FROM homework WHERE completed = 0 AND date > :minDate")
    abstract suspend fun getAllNonCompleted(minDate: Long = Calendar.getInstance().timeInMillis): List<Homework>

    @Query("SELECT * FROM homework WHERE id = :id ORDER BY date")
    abstract suspend fun get(id: Int): Homework

    @Query("SELECT * FROM homework ORDER BY date")
    abstract fun getAll(): LiveData<List<Homework>>

    @Query("SELECT * FROM homework WHERE groupId IN (:groupId) ORDER BY date")
    abstract fun getAllForGroups(groupId: List<Int>): LiveData<List<Homework>>

    @Query("SELECT * FROM homework WHERE groupId = :groupId ORDER BY date")
    protected abstract suspend fun getAllForGroup(groupId: Int): List<Homework>

    @Query("DELETE FROM homework")
    abstract suspend fun deleteAll()

    @Query("DELETE FROM homework WHERE groupId = :groupId")
    abstract suspend fun deleteAllForGroup(groupId: Int)

    @Transaction
    open suspend fun updateAll(homework: List<Homework>) {
        deleteAll()
        insert(homework)
    }

    @Transaction
    open suspend fun updateAllForGroup(groupId: Int, homework: List<Homework>) {
        // Preserve completed state of entries
        val oldHomework = getAllForGroup(groupId).associateBy { it.id }
        homework.forEach {
            it.completed = oldHomework[it.id]?.completed == true
        }

        deleteAllForGroup(groupId)
        insert(homework)
    }
}
