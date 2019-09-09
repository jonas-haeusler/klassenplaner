package gsrv.klassenplaner.ui.list.homework

import android.text.format.DateUtils
import android.widget.LinearLayout
import androidx.core.os.bundleOf
import androidx.interpolator.view.animation.FastOutSlowInInterpolator
import androidx.lifecycle.observe
import androidx.navigation.fragment.findNavController
import gsrv.klassenplaner.R
import gsrv.klassenplaner.data.entities.Homework
import gsrv.klassenplaner.ui.addedit.base.BaseAddEditFragment
import gsrv.klassenplaner.ui.base.BaseListAdapter
import gsrv.klassenplaner.ui.base.init
import gsrv.klassenplaner.ui.base.onClick
import gsrv.klassenplaner.ui.common.DividerItemDecoration
import gsrv.klassenplaner.ui.list.base.BaseListFragment
import gsrv.klassenplaner.util.reverseStrikeThroughAnimation
import gsrv.klassenplaner.util.startStrikeThroughAnimation
import jp.wasabeef.recyclerview.animators.FadeInUpAnimator
import kotlinx.android.synthetic.main.fragment_list.*
import kotlinx.android.synthetic.main.item_homework.view.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class HomeworkFragment : BaseListFragment<Homework>() {
    override val viewModel: HomeworkViewModel by viewModel()
    override lateinit var adapter: BaseListAdapter<Homework>

    override fun setupRecyclerView() {
        val dividerInset = resources.getDimensionPixelOffset(R.dimen.divider_margin)
        val decoration = DividerItemDecoration(requireContext(), LinearLayout.VERTICAL, dividerInset)

        adapter = recyclerView.init(
            resourceId = R.layout.item_homework,
            itemDecoration = decoration,
            itemAnimator = FadeInUpAnimator(FastOutSlowInInterpolator())
        ) { homework ->
            title.text = DateUtils.getRelativeTimeSpanString(
                homework.date.time, System.currentTimeMillis(), DateUtils.DAY_IN_MILLIS
            )
            description1.text = homework.subject
            description2.text = homework.homework
            meta.text = groups.getValue(homework.groupId).name

            completed.isChecked = homework.completed
            if (homework.completed) {
                title.startStrikeThroughAnimation()
                description1.startStrikeThroughAnimation()
                description2.startStrikeThroughAnimation()
            }

            completed.setOnCheckedChangeListener { _, newValue ->
                homework.completed = newValue
                viewModel.setHomeworkCompleted(homework)

                if (newValue) {
                    title.startStrikeThroughAnimation()
                    description1.startStrikeThroughAnimation()
                    description2.startStrikeThroughAnimation()
                } else {
                    title.reverseStrikeThroughAnimation()
                    description1.reverseStrikeThroughAnimation()
                    description2.reverseStrikeThroughAnimation()
                }
            }

            if (groups.getValue(homework.groupId).isLoggedIn && !isOffline()) {
                onClick {
                    findNavController().navigate(
                        R.id.action_listFragment_to_homeworkAddEditFragment,
                        bundleOf(BaseAddEditFragment.ARGUMENT_ITEM_ID to homework.id.toString())
                    )
                }
            }
        }
    }

    override fun onFloatingActionButtonClicked() {
        findNavController().navigate(R.id.action_listFragment_to_homeworkAddEditFragment)
    }
}
