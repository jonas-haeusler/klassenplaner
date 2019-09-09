package gsrv.klassenplaner.data.database.daos

import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update

abstract class BaseDao<T> {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    abstract suspend fun insert(entity: T): Long

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    abstract suspend fun insert(entities: List<T>): List<Long>

    @Update
    abstract suspend fun update(entity: T)

    @Update
    abstract suspend fun update(entities: List<T>)

    @Delete
    abstract suspend fun delete(entity: T)

    @Transaction
    open suspend fun upsert(entity: T) {
        val id = insert(entity)
        if (id == -1L) {
            update(entity)
        }
    }

    @Transaction
    open suspend fun upsert(entities: List<T>) {
        val insertResult = insert(entities)
        val updateList = mutableListOf<T>()
        for (i in 0 until insertResult.size) {
            if (insertResult[i] == -1L) {
                updateList.add(entities[i])
            }
        }

        if (updateList.isNotEmpty()) {
            update(updateList)
        }
    }
}
