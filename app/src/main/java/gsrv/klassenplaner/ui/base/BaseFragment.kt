package gsrv.klassenplaner.ui.base

import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import gsrv.klassenplaner.R

open class BaseFragment : Fragment() {

    /**
     * Sets [toolbar] with the global navigation controller up.
     */
    protected fun setupToolbar(toolbar: Toolbar, hasOptionsMenu: Boolean = false) {
        (requireActivity() as AppCompatActivity).setSupportActionBar(toolbar)
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.listFragment,
                R.id.setupFragment,
                R.id.notificationDialogFragment
            )
        )
        toolbar.setupWithNavController(findNavController(), appBarConfiguration)
        setHasOptionsMenu(hasOptionsMenu)
    }

    /**
     * Convenient function casting [requireActivity] to [AppCompatActivity].
     *
     * Throws an [TypeCastException] when [requireActivity] is not of type [AppCompatActivity].
     */
    fun requireCompatActivity(): AppCompatActivity = requireActivity() as AppCompatActivity
}
