package gsrv.klassenplaner.ui.list

import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.core.view.postDelayed
import androidx.interpolator.view.animation.FastOutSlowInInterpolator
import androidx.lifecycle.observe
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import androidx.viewpager.widget.ViewPager
import com.google.android.material.tabs.TabLayout
import gsrv.klassenplaner.R
import gsrv.klassenplaner.ui.base.BaseFragment
import kotlinx.android.synthetic.main.fragment_list_host.*
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class ListHostFragment : BaseFragment() {
    private val listHostViewModel: ListHostViewModel by viewModel()
    private val globalStateViewModel: GlobalStateViewModel by sharedViewModel()
    private val anim by lazy {
        AnimationUtils.loadAnimation(requireContext(), R.anim.anim_slide_up_alpha).apply {
            interpolator = FastOutSlowInInterpolator()
            duration = 400
        }
    }
    private lateinit var runAfterBlock: () -> Unit
    private var uiInitialized = false

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_list_host, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        uiInitialized = false

        listHostViewModel.groups.observe(this) { result ->
            if (result.data.isNullOrEmpty()) {
                // If we don't have groups, navigate to the setup

                // We can safely ignore the state of the result here because regardless of the result, there
                // should always be cached data available

                // Clear task after navigation, thus the app closes when navigating back from the setup
                val navOptions = NavOptions.Builder().setPopUpTo(R.id.main_nav_graph, true).build()
                findNavController().navigate(R.id.setup_nav_graph, null, navOptions)
            } else {
                setupUi { result.data?.let(globalStateViewModel::setGroups) }
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_list_host, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_manage_lists -> {
                findNavController().navigate(R.id.action_listFragment_to_groupManagerFragment)
                true
            }
            R.id.menu_notifications -> {
                findNavController().navigate(R.id.action_listFragment_to_notificationDialogFragment)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    /**
     * Animates the user interface into place and runs [runAfter] when the animation has ended.
     * If this method is called after the ui was initialized [runAfter] will be invoked immediately.
     * When this method is called during the animation progress, the [runAfter] block will override the old one.
     */
    private fun setupUi(runAfter: () -> Unit) {
        if (!uiInitialized) {
            setupToolbar(toolbar, true)
            setupViewPager()
            setupTabLayout()
            uiInitialized = true
        }

        runAfterBlock = runAfter

        if (anim.hasEnded()) {
            runAfterBlock.invoke()
        } else if (!anim.hasStarted()) {
            animateUi { runAfterBlock.invoke() }
        }
    }

    private fun animateUi(runAfter: () -> Unit) {
        anim.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationEnd(animation: Animation?) {
                runAfter.invoke()
            }

            override fun onAnimationRepeat(animation: Animation?) {}

            override fun onAnimationStart(animation: Animation?) {}
        })

        viewPager.animation = anim
        tabLayout.animation = anim
        toolbar.animation = anim

        anim.start()
    }

    private fun setupViewPager() {
        viewPager.adapter = ListViewPagerAdapter(requireContext(), childFragmentManager)
        viewPager.pageMargin = resources.getDimensionPixelOffset(R.dimen.page_margin)
        viewPager.setPageMarginDrawable(ColorDrawable(ContextCompat.getColor(requireContext(), R.color.colorPrimary)))

        viewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageSelected(position: Int) {
                // let the fab briefly disappear, then reappear
                if (fab.isVisible) {
                    fab.hide()
                    fab.postDelayed(220) {
                        fab.show()
                    }
                }
            }

            override fun onPageScrollStateChanged(state: Int) {}

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {}
        })
    }

    private fun setupTabLayout() {
        tabLayout.setupWithViewPager(viewPager)

        // fixed tabs when screen width < 600dp else scrollable tabs
        val displayMetrics = requireContext().resources.displayMetrics
        if (displayMetrics.widthPixels / displayMetrics.density < 600) {
            tabLayout.tabMode = TabLayout.MODE_FIXED
        } else {
            tabLayout.tabMode = TabLayout.MODE_SCROLLABLE
        }
    }
}
