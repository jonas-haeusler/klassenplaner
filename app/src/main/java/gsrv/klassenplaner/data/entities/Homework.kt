package gsrv.klassenplaner.data.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.CASCADE
import androidx.room.Index
import androidx.room.PrimaryKey
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.util.*

@Entity(
    tableName = "homework",
    foreignKeys = [ForeignKey(
        entity = Group::class,
        parentColumns = ["id"],
        childColumns = ["groupId"],
        onDelete = CASCADE
    )],
    indices = [Index("groupId")]
)
data class Homework(
    @Expose @SerializedName("id") @PrimaryKey override var id: Int,
    @Expose @SerializedName("subject") val subject: String,
    @Expose @SerializedName("homework") val homework: String,
    @Expose @SerializedName("date") val date: Date,
    @Expose(serialize = false) val groupId: Int,
    var completed: Boolean
): BaseItem()
