package gsrv.klassenplaner.ui.setup

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.core.text.isDigitsOnly
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.Observer
import androidx.navigation.NavOptions
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import gsrv.klassenplaner.R
import gsrv.klassenplaner.ui.base.BaseFragment
import kotlinx.android.synthetic.main.fragment_login.*
import kotlinx.android.synthetic.main.layout_toolbar.toolbar
import org.koin.androidx.viewmodel.ext.android.viewModel

class LoginFragment : BaseFragment() {
    private val setupViewModel: SetupViewModel by viewModel()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_login, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupToolbar(toolbar)

        meta.setOnClickListener { setupViewModel.login(groupId.text.toString()) }
        groupId.addTextChangedListener { meta.isEnabled = it?.length == 6 }
        groupId.setOnEditorActionListener { _, actionId, _ ->
            return@setOnEditorActionListener when (actionId) {
                EditorInfo.IME_ACTION_SEND -> {
                    meta.performClick()
                    true
                }
                else -> false
            }
        }

        setupViewModel.authenticationState.observe(this, Observer { authenticationState ->
            meta.isEnabled = false
            if (authenticationState == SetupViewModel.AuthenticationState.AUTHENTICATED) {
                val navExtras = FragmentNavigatorExtras(backdrop to "backdrop")
                val navOptions = NavOptions.Builder().setPopUpTo(R.id.main_nav_graph, true).build()
                findNavController().navigate(R.id.main_nav_graph, null, navOptions, navExtras)
            } else if (authenticationState == SetupViewModel.AuthenticationState.AUTHENTICATING) {
                meta.isEnabled = false
            } else if (authenticationState == SetupViewModel.AuthenticationState.INVALID_AUTHENTICATION) {
                showError(true)
            }
        })

        arguments?.let {
            val id = it.getString("group_id", "")
            if (id.isDigitsOnly()) {
                groupId.post {
                    groupId.setText(id)
                }
            }
        }
    }

    private fun showError(showError: Boolean) {
        textInputLayout.error = "Invalid group id"
        textInputLayout.isErrorEnabled = showError
    }
}
