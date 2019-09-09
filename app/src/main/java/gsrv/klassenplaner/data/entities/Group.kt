package gsrv.klassenplaner.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.Expose

@Entity(tableName = "groups")
data class Group(
    @Expose(serialize = false) @PrimaryKey override var id: Int = -1,
    @Expose val name: String,
    @Expose(serialize = false) var password: String? = null
): BaseItem() {

    val isLoggedIn get() = !password.isNullOrBlank()
}
