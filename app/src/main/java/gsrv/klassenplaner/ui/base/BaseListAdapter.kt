package gsrv.klassenplaner.ui.base

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.annotation.LayoutRes
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import gsrv.klassenplaner.data.entities.BaseItem

class BaseListAdapter<T>(
    diffUtil: DiffUtil.ItemCallback<T>,
    @LayoutRes private val resourceId: Int,
    private val bindViewHolder: View.(T) -> Unit
) : ListAdapter<T, BaseListAdapter.ViewHolder>(diffUtil) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ViewHolder(LayoutInflater.from(parent.context).inflate(resourceId, parent, false))

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.itemView.bindViewHolder(getItem(position))
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
}

fun <T : BaseItem> RecyclerView.init(
    diffUtil: BaseDiffUtil<T> = BaseDiffUtil(),
    resourceId: Int = android.R.layout.simple_list_item_1,
    manager: RecyclerView.LayoutManager = LinearLayoutManager(context),
    itemDecoration: RecyclerView.ItemDecoration = DividerItemDecoration(context, LinearLayout.VERTICAL),
    itemAnimator: RecyclerView.ItemAnimator = DefaultItemAnimator(),
    bindViewHolder: View.(T) -> Unit
): BaseListAdapter<T> {
    return BaseListAdapter(diffUtil, resourceId, bindViewHolder).apply {
        adapter = this
        layoutManager = manager
        addItemDecoration(itemDecoration)
        setItemAnimator(itemAnimator)
    }
}

fun View.onClick(onClick: () -> Unit) {
    setOnClickListener { onClick.invoke() }
}

fun View.onLongClick(onClick: () -> Unit) {
    setOnLongClickListener { onClick.invoke(); true }
}
