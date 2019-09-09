package gsrv.klassenplaner.ui.addedit

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Filter
import android.widget.TextView
import gsrv.klassenplaner.R
import gsrv.klassenplaner.data.entities.Group

class GroupAdapter(context: Context) : ArrayAdapter<Group>(context, 0) {
    private var items: List<Group> = emptyList()
    private val filter = NoFilter()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view =
            convertView ?: LayoutInflater.from(context).inflate(R.layout.item_exposed_dropdown_menu, parent, false)

        val group = getItem(position)
        (view as TextView).text = group?.name

        return view
    }

    fun setItems(groups: List<Group>) {
        if (groups != items) {
            clear()
            addAll(groups)
            items = groups
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
