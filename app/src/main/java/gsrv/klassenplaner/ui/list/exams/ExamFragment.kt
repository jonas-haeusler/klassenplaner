package gsrv.klassenplaner.ui.list.exams

import android.text.format.DateUtils
import android.widget.LinearLayout
import androidx.core.os.bundleOf
import androidx.interpolator.view.animation.FastOutSlowInInterpolator
import androidx.lifecycle.observe
import androidx.navigation.fragment.findNavController
import gsrv.klassenplaner.R
import gsrv.klassenplaner.data.entities.Exam
import gsrv.klassenplaner.ui.addedit.base.BaseAddEditFragment
import gsrv.klassenplaner.ui.base.BaseListAdapter
import gsrv.klassenplaner.ui.base.init
import gsrv.klassenplaner.ui.base.onClick
import gsrv.klassenplaner.ui.common.DividerItemDecoration
import gsrv.klassenplaner.ui.list.base.BaseListFragment
import jp.wasabeef.recyclerview.animators.FadeInUpAnimator
import kotlinx.android.synthetic.main.fragment_list.*
import kotlinx.android.synthetic.main.item_exam.view.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class ExamFragment : BaseListFragment<Exam>() {
    override val viewModel: ExamViewModel by viewModel()
    override lateinit var adapter: BaseListAdapter<Exam>

    override fun setupRecyclerView() {
        val dividerInset = resources.getDimensionPixelOffset(R.dimen.divider_margin)
        val decoration = DividerItemDecoration(requireContext(), LinearLayout.VERTICAL, dividerInset)

        adapter = recyclerView.init(
            resourceId = R.layout.item_exam,
            itemDecoration = decoration,
            itemAnimator = FadeInUpAnimator(FastOutSlowInInterpolator())
        ) { exam ->
            title.text = DateUtils.getRelativeTimeSpanString(
                exam.date.time, System.currentTimeMillis(), DateUtils.DAY_IN_MILLIS
            )
            description1.text = exam.subject
            description2.text = exam.exam
            meta.text = groups.getValue(exam.groupId).name

            if (groups.getValue(exam.groupId).isLoggedIn && !isOffline()) {
                onClick {
                    findNavController().navigate(
                        R.id.action_listFragment_to_examAddEditFragment,
                        bundleOf(BaseAddEditFragment.ARGUMENT_ITEM_ID to exam.id.toString())
                    )
                }
            }

        }
    }

    override fun onFloatingActionButtonClicked() {
        findNavController().navigate(R.id.action_listFragment_to_examAddEditFragment)
    }
}

