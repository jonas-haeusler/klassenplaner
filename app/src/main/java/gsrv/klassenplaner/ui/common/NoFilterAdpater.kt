package gsrv.klassenplaner.ui.common

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Filter
import android.widget.TextView
import gsrv.klassenplaner.R

class NoFilterAdpater<T>(context: Context) : ArrayAdapter<T>(context, 0) {
    private var items: List<T> = emptyList()
    private val filter = NoFilter()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = convertView ?: LayoutInflater.from(context).inflate(R.layout.item_exposed_dropdown_menu, parent, false)

        val item = getItem(position)
        (view as TextView).text = item.toString()

        return view
    }

    fun setItems(newItems: List<T>) {
        if (newItems != items) {
            clear()
            addAll(newItems)
            items = newItems
        }
    }

    override fun getFilter(): Filter {
        return filter
    }

    private inner class NoFilter : Filter() {

        override fun performFiltering(constraint: CharSequence?): FilterResults {
            return FilterResults().apply {
                values = items
                count = items.size
            }
        }

        override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
            notifyDataSetChanged()
        }

    }
}
