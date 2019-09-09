package gsrv.klassenplaner.data.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.util.*

@Entity(
    tableName = "exams",
    foreignKeys = [ForeignKey(
        entity = Group::class,
        parentColumns = ["id"],
        childColumns = ["groupId"],
        onDelete = ForeignKey.CASCADE
    )],
    indices = [Index("groupId")]
)
data class Exam(
    @Expose @SerializedName("id") @PrimaryKey(autoGenerate = false) override var id: Int,
    @Expose @SerializedName("subject") val subject: String,
    @Expose @SerializedName("exam") val exam: String,
    @Expose @SerializedName("date") val date: Date,
    @Expose(serialize = false) val groupId: Int
): BaseItem()
