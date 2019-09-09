package gsrv.klassenplaner.ui.groupmanager

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.interpolator.view.animation.FastOutSlowInInterpolator
import androidx.lifecycle.observe
import androidx.navigation.fragment.findNavController
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import gsrv.klassenplaner.data.entities.Group
import gsrv.klassenplaner.ui.base.BaseFragment
import gsrv.klassenplaner.ui.base.BaseListAdapter
import gsrv.klassenplaner.ui.base.init
import gsrv.klassenplaner.ui.groupmanager.GroupManagerViewModel.*
import jp.wasabeef.recyclerview.animators.FadeInUpAnimator
import kotlinx.android.synthetic.main.fragment_group_mananger.emptyView
import kotlinx.android.synthetic.main.fragment_group_mananger.fab
import kotlinx.android.synthetic.main.fragment_group_mananger.loadingView
import kotlinx.android.synthetic.main.fragment_group_mananger.recyclerView
import kotlinx.android.synthetic.main.item_group.view.copy
import kotlinx.android.synthetic.main.item_group.view.delete
import kotlinx.android.synthetic.main.item_group.view.iconContainer
import kotlinx.android.synthetic.main.item_group.view.login
import kotlinx.android.synthetic.main.item_group.view.password
import kotlinx.android.synthetic.main.item_group.view.share
import kotlinx.android.synthetic.main.item_group.view.text
import kotlinx.android.synthetic.main.item_group.view.title
import kotlinx.android.synthetic.main.layout_password_input.view.passwordInput
import kotlinx.android.synthetic.main.layout_toolbar.toolbar
import org.koin.androidx.viewmodel.ext.android.viewModel
import android.content.ClipData
import android.content.Context.CLIPBOARD_SERVICE
import android.content.ClipboardManager
import gsrv.klassenplaner.BuildConfig
import gsrv.klassenplaner.R

class GroupManagerFragment : BaseFragment() {

    private val viewModel: GroupManagerViewModel by viewModel()

    private lateinit var adapter: BaseListAdapter<Group>

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_group_mananger, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupToolbar(toolbar)
        setupRecyclerView()
        setupFab()
        observeViewModel()
    }

    private fun observeViewModel() {
        viewModel.viewState.observe(viewLifecycleOwner, ::render)
        viewModel.command.observe(viewLifecycleOwner, ::processCommand)
    }

    private fun render(viewState: ViewState) {
        adapter.submitList(viewState.groups)
        loadingView.isVisible = viewState.isLoading
        emptyView.isVisible = viewState.isEmpty
    }

    private fun processCommand(command: Command?) {
        when (command) {
            is Command.Close -> {
                Toast.makeText(requireContext(), command.message, Toast.LENGTH_LONG).show()
                findNavController().navigateUp()
            }
        }
    }

    private fun setupFab() {
        fab.setOnClickListener {
            findNavController().navigate(R.id.setup_nav_graph)
        }
    }

    private fun setupRecyclerView() {
        adapter = recyclerView.init(
            resourceId = R.layout.item_group,
            itemAnimator = FadeInUpAnimator(FastOutSlowInInterpolator())
        ) { group ->
            title.text = group.name
            text.text = getString(if (group.isLoggedIn) R.string.logged_in else R.string.not_logged_in)
            login.text = getString(if (group.isLoggedIn) R.string.logout else R.string.login)
            password.text = getString(R.string.password).format(group.password)
            password.isVisible = group.isLoggedIn
            copy.isVisible = group.isLoggedIn

            login.setOnClickListener {
                if (group.isLoggedIn) {
                    showLogoutDialog(group)
                } else {
                    showLoginDialog(group)
                }
            }

            delete.setOnClickListener {
                showDeleteDialog(group)
            }

            copy.setOnClickListener {
                val clipboard = requireContext().getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
                val clip = ClipData.newPlainText("group-password", group.password)
                clipboard.setPrimaryClip(clip)
                
                Toast.makeText(requireContext(), requireContext().getString(R.string.password_copied), Toast.LENGTH_SHORT).show()
            }

            share.setOnClickListener {
                val sendIntent: Intent = Intent().apply {
                    action = Intent.ACTION_SEND
                    putExtra(Intent.EXTRA_TEXT, BuildConfig.BASE_URL + "groups/${group.id}")
                    type = "text/plain"
                }
                startActivity(Intent.createChooser(sendIntent, resources.getText(R.string.send_to)))
            }

            setOnClickListener {
                iconContainer.isVisible = !iconContainer.isVisible
            }
        }
    }

    private fun showDeleteDialog(group: Group) {
        val builder = MaterialAlertDialogBuilder(requireContext())
            .setMessage(getString(R.string.remove_group, group.name))
            .setNegativeButton(R.string.cancel) { dialog, _ ->
                dialog.dismiss()
            }

        if (group.isLoggedIn) {
            builder.setPositiveButton(getString(R.string.group_remove_locally)) { _, _ ->
                viewModel.deleteLocal(group)
            }
            builder.setNeutralButton(getString(R.string.group_delete_globally)) { _, _ ->
                viewModel.delete(group)
            }
        } else {
            builder.setPositiveButton(R.string.remove) { _, _ ->
                viewModel.deleteLocal(group)
            }
        }

        builder.create().show()
    }

    private fun showLoginDialog(group: Group) {
        val view = View.inflate(requireContext(), R.layout.layout_password_input, null)

        MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.login)
            .setView(view)
            .setPositiveButton(R.string.login) { _, _ ->
                viewModel.login(group, view.passwordInput.text.toString())
            }
            .setNegativeButton(R.string.cancel) { dialog, _ ->
                dialog.dismiss()
            }
            .create()
            .show()
    }

    private fun showLogoutDialog(group: Group) {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.logout)
            .setPositiveButton(R.string.logout) { _, _ ->
                viewModel.logout(group)
            }
            .setNegativeButton(R.string.cancel) { dialog, _ ->
                dialog.dismiss()
            }
            .create()
            .show()
    }
}
