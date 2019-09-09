package gsrv.klassenplaner.data.repository

import androidx.lifecycle.LiveData
import gsrv.klassenplaner.data.Result
import gsrv.klassenplaner.data.entities.BaseItem
import gsrv.klassenplaner.data.entities.Group

interface BaseRepository<T: BaseItem> {

    /**
     * Get a list of items wrapped into a [Result] object for all [groups] from
     * both, the database and network.
     */
    fun get(groups: List<Group>): LiveData<Result<List<T>>>

    /**
     * Get a single item from the database.
     */
    suspend fun get(itemId: Int): T

    suspend fun create(item: T)

    suspend fun update(item: T)

    suspend fun delete(item: T)

    suspend fun refetch(groups: List<Group>)
}
