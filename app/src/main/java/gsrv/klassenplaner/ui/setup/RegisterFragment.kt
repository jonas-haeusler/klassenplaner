package gsrv.klassenplaner.ui.setup

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.Observer
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import gsrv.klassenplaner.R
import gsrv.klassenplaner.ui.base.BaseFragment
import kotlinx.android.synthetic.main.fragment_register.*
import kotlinx.android.synthetic.main.layout_toolbar.toolbar
import org.koin.androidx.viewmodel.ext.android.viewModel

class RegisterFragment : BaseFragment() {
    private val setupViewModel: SetupViewModel by viewModel()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_register, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupToolbar(toolbar)

        register.setOnClickListener { setupViewModel.register(groupName.text.toString()) }
        groupName.addTextChangedListener { register.isEnabled = it?.length in 3..24 }
        groupName.setOnEditorActionListener { _, actionId, _ ->
            return@setOnEditorActionListener when (actionId) {
                EditorInfo.IME_ACTION_SEND -> {
                    register.performClick()
                    true
                }
                else -> false
            }
        }

        setupViewModel.authenticationState.observe(this, Observer { authenticationState ->
            register.isEnabled = false
            if (authenticationState == SetupViewModel.AuthenticationState.AUTHENTICATED) {
                val navOptions = NavOptions.Builder().setPopUpTo(R.id.setup_nav_graph, true).build()
                findNavController().navigate(R.id.main_nav_graph, null, navOptions)
            } else if (authenticationState == SetupViewModel.AuthenticationState.AUTHENTICATING) {
                register.isEnabled = false
            } else if (authenticationState == SetupViewModel.AuthenticationState.INVALID_AUTHENTICATION) {
                showError(true)
            }
        })
    }

    private fun showError(showError: Boolean) {
        textInputLayout.error = "Invalid group name"
        textInputLayout.isErrorEnabled = showError
    }
}
