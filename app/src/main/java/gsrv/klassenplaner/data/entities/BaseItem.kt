package gsrv.klassenplaner.data.entities

/**
 * A basic item backed by an [id] property.
 */
abstract class BaseItem {
    abstract var id: Int

    override fun equals(other: Any?): Boolean {
        if (this === other) {
            return true
        }

        if (other is BaseItem && id == other.id) {
            return true
        }

        return false
    }

    override fun hashCode(): Int {
        return id
    }
}
