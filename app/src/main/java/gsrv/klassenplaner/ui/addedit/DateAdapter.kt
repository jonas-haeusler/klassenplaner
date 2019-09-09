package gsrv.klassenplaner.ui.addedit

import android.content.Context
import android.widget.ArrayAdapter
import android.widget.Filter
import androidx.annotation.LayoutRes
import gsrv.klassenplaner.R

open class DateAdapter<T>(
    context: Context,
    @LayoutRes resId: Int = R.layout.item_exposed_dropdown_menu,
    val items: List<T>
) : ArrayAdapter<T>(context, resId, items) {
    private val filter = NoFilter()

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
