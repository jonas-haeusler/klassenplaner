package gsrv.klassenplaner.data.database.daos

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import gsrv.klassenplaner.data.entities.Group

@Dao
abstract class GroupDao : BaseDao<Group>() {
    @Query("SELECT * FROM groups")
    abstract fun getAll(): LiveData<List<Group>>

    @Query("SELECT * FROM groups WHERE password <> ''")
    abstract fun getAllAuthorized(): LiveData<List<Group>>

    @Query("SELECT * FROM groups")
    abstract suspend fun getAllAsync(): List<Group>

    @Query("SELECT * FROM groups WHERE password <> ''")
    abstract suspend fun getAllAuthorizedAsync(): List<Group>

    @Query("SELECT * FROM groups WHERE id = :groupId")
    abstract fun get(groupId: Int): LiveData<Group>

    @Query("DELETE FROM groups WHERE id = :groupId")
    abstract suspend fun delete(groupId: Int): Int
}
