package gsrv.klassenplaner.ui.base

import androidx.recyclerview.widget.DiffUtil
import gsrv.klassenplaner.data.entities.BaseItem

class BaseDiffUtil<T: BaseItem> : DiffUtil.ItemCallback<T>() {
    override fun areItemsTheSame(oldItem: T, newItem: T) =
        oldItem.id == newItem.id

    override fun areContentsTheSame(oldItem: T, newItem: T) = oldItem == newItem
}
