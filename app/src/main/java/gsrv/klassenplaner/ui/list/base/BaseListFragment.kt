package gsrv.klassenplaner.ui.list.base

import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.observe
import androidx.recyclerview.widget.RecyclerView
import gsrv.klassenplaner.R
import gsrv.klassenplaner.data.entities.BaseItem
import gsrv.klassenplaner.data.entities.Group
import gsrv.klassenplaner.ui.base.BaseFragment
import gsrv.klassenplaner.ui.base.BaseListAdapter
import gsrv.klassenplaner.ui.list.GlobalStateViewModel
import gsrv.klassenplaner.ui.list.ListHostFragment
import kotlinx.android.synthetic.main.fragment_list.emptyView
import kotlinx.android.synthetic.main.fragment_list.errorView
import kotlinx.android.synthetic.main.fragment_list.loadingView
import kotlinx.android.synthetic.main.fragment_list.recyclerView
import kotlinx.android.synthetic.main.fragment_list.swipeRefreshLayout
import kotlinx.android.synthetic.main.fragment_list_host.fab
import kotlinx.android.synthetic.main.layout_banner.banner
import kotlinx.android.synthetic.main.layout_banner.bannerRetry
import kotlinx.android.synthetic.main.layout_banner.bannerSettings
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

private const val REQUEST_CODE_CONNECTIVITY_SETTINGS = 1337

/**
 * Base [Fragment] for child fragments of [ListHostFragment].
 */
abstract class BaseListFragment<T : BaseItem> : BaseFragment() {
    private val globalStateViewModel: GlobalStateViewModel by sharedViewModel()

    protected abstract val viewModel: BaseListViewModel<T>

    /**
     * Maps groupIds to [Group]s.
     */
    protected lateinit var groups: Map<Int, Group>

    /**
     * [BaseListAdapter] for the [RecyclerView] inside [BaseListFragment]s layout.
     */
    protected abstract var adapter: BaseListAdapter<T>

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        preSetupRecyclerView()
        setupOfflineModeBanner()
        swipeRefreshLayout.setOnRefreshListener { refresh() }
        viewModel.viewState.observe(viewLifecycleOwner, ::render)
        globalStateViewModel.groups.observe(viewLifecycleOwner, ::setGroups)
    }

    override fun onResume() {
        super.onResume()

        // called whenever this fragment gets visible in the ViewPager
        // we want to take over the host-fragments FAB action here
        requireParentFragment().fab.setOnClickListener {
            onFloatingActionButtonClicked()
        }
    }

    protected fun isOffline() = viewModel.viewState.value?.isOffline ?: true

    /**
     * Renders a given [viewState] onto the default [BaseListFragment] layout.
     */
    private fun render(viewState: ListViewState<T>) = with(viewState) {
        emptyView.isVisible = isContentEmpty
        loadingView.isVisible = isContentLoading
        swipeRefreshLayout.isRefreshing = isContentRefreshing
        errorView.isVisible = showNetworkError
        showOfflineModeBanner(isOffline)
        this@BaseListFragment.groups = groups.associateBy { group -> group.id }
        adapter.submitList(items)
        requireParentFragment().fab.isVisible = hasLoggedInGroups && !isOffline && !isOffline
    }

    /**
     * Whether a banner indicating the offline mode is [visible].
     */
    private fun showOfflineModeBanner(visible: Boolean) {
        recyclerView.itemAnimator?.isRunning {
            banner.isVisible = visible
        }
    }

    private fun setupOfflineModeBanner() {
        bannerRetry.setOnClickListener {
            refresh()
        }

        bannerSettings.setOnClickListener {
            val connectivitySettingsIntent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                Intent(Settings.Panel.ACTION_INTERNET_CONNECTIVITY)
            } else {
                Intent(Settings.ACTION_WIRELESS_SETTINGS)
            }

            startActivityForResult(connectivitySettingsIntent, REQUEST_CODE_CONNECTIVITY_SETTINGS)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_CONNECTIVITY_SETTINGS) {
            refresh()
        }
    }

    private fun refresh() {
        viewModel.retry()
    }

    private fun setGroups(groups: List<Group>) {
        viewModel.setGroups(groups)
    }

    private fun preSetupRecyclerView() {
        var hidden = true
        val elevation = resources.getDimensionPixelOffset(R.dimen.banner_elevation).toFloat()
        val showElevationAnim = ObjectAnimator.ofFloat(banner, "elevation", elevation)
        val hideElevationAnim = ObjectAnimator.ofFloat(banner, "elevation", 0f)

        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                val verticalScrollOffset = recyclerView.computeVerticalScrollOffset()
                if (verticalScrollOffset == 0 && !hidden) {
                    hideElevationAnim.start()
                    hidden = true
                } else if (verticalScrollOffset > 0 && hidden) {
                    showElevationAnim.start()
                    hidden = false
                }
            }
        })

        setupRecyclerView()
    }

    /**
     * Called whenever the host-fragments FloatingActionButton was clicked and this fragment is visible
     */
    protected open fun onFloatingActionButtonClicked() {}

    /**
     * Setup the [RecyclerView] found inside [BaseListFragment]s default layout.
     *
     * @see adapter
     */
    protected abstract fun setupRecyclerView()
}
